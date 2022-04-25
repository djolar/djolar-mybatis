/**
 * Copyright (c) 2022, enix223@163.com All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.enixyu.djolar.mybatis.parser;

import com.enixyu.djolar.mybatis.annotation.Column;
import com.enixyu.djolar.mybatis.annotation.Mapping;
import com.enixyu.djolar.mybatis.annotation.Table;
import com.enixyu.djolar.mybatis.dialect.Dialect;
import com.enixyu.djolar.mybatis.dialect.MySQLDialect;
import com.enixyu.djolar.mybatis.dialect.PostgreSQLDialect;
import com.enixyu.djolar.mybatis.exceptions.DjolarParserException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

public class DjolarParser {

  private static final Map<String, Class<? extends Dialect>> dialectMapping;
  private static final Map<String, QueryMapping> cachedQueryMapping;

  static {
    dialectMapping = new HashMap<>();
    dialectMapping.put("mysql", MySQLDialect.class);
    dialectMapping.put("postgresql", PostgreSQLDialect.class);

    cachedQueryMapping = new ConcurrentHashMap<>();
  }

  private final Pattern queryPattern = Pattern.compile("(\\w+)__(\\w+)__(.*)");
  private final Pattern orderByPattern = Pattern.compile("^([-+])?(.*)$");
  private final Pattern mapperIdPattern = Pattern.compile("^([\\w.]+)\\.(\\w+)$");
  private volatile Dialect dialect;

  private static void reduceFieldMapping(Field field, QueryMapping queryMapping, String tableName) {
    Column column = field.getAnnotation(Column.class);
    if (column == null) {
      queryMapping.set(
          field.getName(),
          new QueryMapping.Item(tableName, field.getName(), field.getType())
      );
      return;
    }

    tableName = column.tableName().equals("") ? tableName : column.tableName();
    String fieldName = column.columnName().equals("") ? field.getName() : column.columnName();
    queryMapping.set(
        column.queryAlias(),
        new QueryMapping.Item(tableName, fieldName, field.getType())
    );
  }

  public ParseResult parse(MappedStatement ms, BoundSql boundSql, QueryRequest request)
      throws DjolarParserException {
    Matcher matcher = mapperIdPattern.matcher(ms.getId());
    if (!matcher.find() || matcher.groupCount() != 2) {
      // skip djolar interceptor
      return new ParseResult(boundSql, request);
    }

    String mapperClzName = matcher.group(1);
    String mapperMethod = matcher.group(2);
    Class<?> mapperClass;
    try {
      mapperClass = Class.forName(mapperClzName);
    } catch (ClassNotFoundException e) {
      // skip djolar interceptor
      return new ParseResult(boundSql, request);
    }

    // try to get filed mapping from method
    Mapping mappingAnnotation;
    try {
      Method method = mapperClass.getMethod(mapperMethod, QueryRequest.class);
      mappingAnnotation = method.getAnnotation(Mapping.class);
      if (mappingAnnotation == null) {
        // get field mapping from class
        mappingAnnotation = mapperClass.getAnnotation(Mapping.class);
      }
    } catch (NoSuchMethodException e) {
      // skip djolar interceptor
      return new ParseResult(boundSql, request);
    }

    if (mappingAnnotation == null) {
      // skip djolar interceptor
      return new ParseResult(boundSql, request);
    }

    ensureDialect(ms);

    QueryMapping queryMapping = loadQueryMapping(ms.getId(), mappingAnnotation.value());
    List<ParameterMapping> parameterMappings = new ArrayList<>();
    Map<String, Object> parameterObject = new HashMap<>();
    List<WhereClause> whereClauseList = parseQueryFields(
        request.getQuery(),
        queryMapping,
        ms,
        parameterMappings,
        parameterObject);
    List<String> orderByClauseList = parseOrderByFields(request.getSort());

    String sql = boundSql.getSql();
    StringBuilder sqlbuilder = new StringBuilder(sql);
    if (whereClauseList != null && whereClauseList.size() > 0) {
      sqlbuilder.append(" WHERE ");
      String where = whereClauseList.stream().map(dialect::buildWhere)
          .collect(Collectors.joining(" AND "));
      sqlbuilder.append(where);
    }
    if (orderByClauseList != null && orderByClauseList.iterator().hasNext()) {
      sqlbuilder.append(" ORDER BY ");
      orderByClauseList.forEach(sqlbuilder::append);
    }
    BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sqlbuilder.toString(),
        parameterMappings, parameterObject);
    return new ParseResult(newBoundSql, parameterObject);
  }

  private QueryMapping loadQueryMapping(String id, Class<?> fieldMappingClass) {
    Table tableNameAnnotation = fieldMappingClass.getAnnotation(Table.class);
    String tableName = tableNameAnnotation != null ? tableNameAnnotation.value()
        : fieldMappingClass.getName().toLowerCase();
    return cachedQueryMapping.computeIfAbsent(id, k -> {
      QueryMapping mapping = new QueryMapping();
      List<Field> allFields = getAllFields(fieldMappingClass);
      allFields.forEach(f -> reduceFieldMapping(f, mapping, tableName));
      return mapping;
    });
  }

  private void ensureDialect(MappedStatement ms) throws DjolarParserException {
    if (dialect != null) {
      return;
    }

    synchronized (dialectMapping) {
      if (dialect != null) {
        return;
      }

      String jdbcUrl = getJdbcUrl(ms);
      String[] tokens = jdbcUrl.split(":");
      if (tokens.length < 2) {
        throw new DjolarParserException("invalid jdbc url");
      }

      Class<? extends Dialect> dialectClz = dialectMapping.get(tokens[1]);
      try {
        dialect = dialectClz.getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
               InvocationTargetException e) {
        throw new DjolarParserException("failed to initialize dialect", e);
      }
    }
  }

  private String getJdbcUrl(MappedStatement ms) throws DjolarParserException {
    DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
    try (Connection connection = dataSource.getConnection()) {
      return connection.getMetaData().getURL();
    } catch (SQLException e) {
      throw new DjolarParserException("failed to get jdbc url", e);
    }
  }

  private List<WhereClause> parseQueryFields(String query,
      QueryMapping queryMapping,
      MappedStatement ms,
      List<ParameterMapping> parameterMappings,
      Map<String, Object> parameterObject) {
    if (query == null) {
      return null;
    }
    return Arrays.stream(query.split("\\|"))
        .map(i -> parseQueryItem(i, queryMapping, ms, parameterMappings, parameterObject))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private WhereClause parseQueryItem(String item,
      QueryMapping queryMapping,
      MappedStatement ms,
      List<ParameterMapping> parameterMappings,
      Map<String, Object> parameterObject) {
    Matcher matcher = queryPattern.matcher(item);
    if (!matcher.find()) {
      return null;
    }

    String fieldName = matcher.group(1);
    String op = matcher.group(2);
    String value = matcher.group(3);
    QueryMapping.Item field = queryMapping.get(fieldName);

    Op operator = Op.fromString(op);
    if (operator == null || field == null) {
      return null;
    }

    Object parsedValue;
    if (field.getFieldType().isPrimitive()) {
      switch (field.getFieldType().getName()) {
        case "int":
          parsedValue = Integer.parseInt(value);
          break;
        case "boolean":
          parsedValue = Boolean.parseBoolean(value);
          break;
        case "long":
          parsedValue = Long.parseLong(value);
          break;
        case "float":
          parsedValue = Float.parseFloat(value);
          break;
        case "double":
          parsedValue = Double.parseDouble(value);
          break;
        case "short":
          parsedValue = Short.parseShort(value);
          break;
        default:
          // unsupported primitive type
          throw new DjolarParserException("unsupported primitive type");
      }
    } else if (field.getFieldType().equals(String.class)) {
      if (operator == Op.Contain) {
        parsedValue = String.format("%%%s%%", value);
      } else if (operator == Op.StartsWith) {
        parsedValue = String.format("%s%%", value);
      } else if (operator == Op.EndsWith) {
        parsedValue = String.format("%%%s", value);
      } else {
        parsedValue = value;
      }
    } else {
      return null;
    }

    String property = field.getTableName() + "_" + field.getFieldName();
    ParameterMapping parameterMapping = new ParameterMapping.Builder(
        ms.getConfiguration(),
        property,
        field.getFieldType()).build();
    parameterMappings.add(parameterMapping);
    parameterObject.put(property, parsedValue);
    return new WhereClause(field.getTableName(), field.getFieldName(), operator, parsedValue,
        field.getFieldType());
  }

  private List<String> parseOrderByFields(String orderBy) {
    if (orderBy == null) {
      return null;
    }

    String[] tokens = orderBy.split(",");
    return Arrays.stream(tokens).map(part -> {
      Matcher matcher = orderByPattern.matcher(part);
      if (!matcher.find()) {
        return null;
      }
      return (matcher.group(1) != null && "-".equals(matcher.group(1)))
          ? matcher.group(2) + " DESC"
          : matcher.group(2) + " ASC";
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  private List<Field> getAllFields(Class<?> cls) {
    List<Field> fields = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
    // get super class fields
    cls = cls.getSuperclass();
    while (cls != null && cls != Object.class) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    return fields;
  }
}
