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

import com.enixyu.djolar.mybatis.annotation.AdditionalSort;
import com.enixyu.djolar.mybatis.annotation.AdditionalWhere;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    Method method;
    try {
      method = mapperClass.getMethod(mapperMethod, QueryRequest.class);
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

    // add extra where and sort
    AdditionalWhere additionalWhere = method.getAnnotation(AdditionalWhere.class);
    if (additionalWhere != null) {
      String query = Optional.ofNullable(request.getQuery())
        .map(String::trim)
        .map(q -> q.length() == 0 ? null : q)
        .map(q -> q + "|" + additionalWhere.where())
        .orElse(additionalWhere.where());
      request.setQuery(query);
    }

    AdditionalSort additionalSort = method.getAnnotation(AdditionalSort.class);
    if (additionalSort != null) {
      String sort = Optional.ofNullable(request.getSort())
        .map(String::trim)
        .map(s -> s.length() == 0 ? null : s)
        .map(s -> s + "," + additionalSort.sort())
        .orElse(additionalSort.sort());
      request.setSort(sort);
    }

    ensureDialect(ms);

    QueryMapping queryMapping = loadQueryMapping(ms.getId(), mappingAnnotation.value());
    List<ParameterMapping> parameterMappings = new ArrayList<>();
    Map<String, Object> parameterObject = new HashMap<>();
    Map<String, Object> additionalParameters = new HashMap<>();

    // parse where clause
    List<WhereClause> whereClauseList = parseQueryFields(
      request.getQuery(),
      queryMapping,
      ms,
      parameterMappings,
      parameterObject,
      additionalParameters);

    // parse order by clause
    List<String> orderByClauseList = parseOrderByFields(request.getSort());

    // build new bound sql with where clauses and order clauses
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
      sqlbuilder.append(String.join(",", orderByClauseList));
    }
    BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sqlbuilder.toString(),
      parameterMappings, parameterObject);
    additionalParameters.keySet()
      .forEach(k -> newBoundSql.setAdditionalParameter(k, additionalParameters.get(k)));
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

  /**
   * Parse query field
   *
   * @param query                query value
   * @param queryMapping         query mapping
   * @param ms                   Mapped statement
   * @param parameterMappings    parameter mapping list
   * @param parameterObject      parameter map
   * @param additionalParameters additional parameters
   * @return WhereClause list
   */
  private List<WhereClause> parseQueryFields(String query,
    QueryMapping queryMapping,
    MappedStatement ms,
    List<ParameterMapping> parameterMappings,
    Map<String, Object> parameterObject,
    Map<String, Object> additionalParameters) throws DjolarParserException {
    if (query == null) {
      return null;
    }
    String[] tokens = query.split("\\|");
    return IntStream.range(0, tokens.length)
      .mapToObj(
        i -> parseQueryItem(i, tokens[i], queryMapping, ms, parameterMappings, parameterObject,
          additionalParameters))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  /**
   * Parse individual query clause
   *
   * @param index                clause index
   * @param item                 clause string value
   * @param queryMapping         query mapping
   * @param ms                   Mapped statement
   * @param parameterMappings    parameter mapping list
   * @param parameterObject      parameter map
   * @param additionalParameters additional parameters
   * @return WhereClause
   */
  @SuppressWarnings("unchecked")
  private WhereClause parseQueryItem(int index,
    String item,
    QueryMapping queryMapping,
    MappedStatement ms,
    List<ParameterMapping> parameterMappings,
    Map<String, Object> parameterObject,
    Map<String, Object> additionalParameters) throws DjolarParserException {
    String[] groups = item.split("__");
    if (groups.length != 2 && groups.length != 3) {
      return null;
    }

    // get field
    String fieldName = groups[0];
    QueryMapping.Item field = queryMapping.get(fieldName);
    if (field == null) {
      throw new DjolarParserException(String.format("'%s' not found in query mapping", fieldName));
    }

    // get operator
    String op = groups[1];
    Op operator = Op.fromString(op);
    if (operator == null) {
      throw new DjolarParserException(
        String.format("operator '%s' is not supported by djolar", op));
    }

    if (groups.length == 2) {
      // clause without value case
      return new WhereClause(field.getTableName(), field.getFieldName(), operator, null,
        field.getFieldType());
    }

    // parse value
    String value = groups[2];
    Class<?> fieldType = field.getFieldType();
    Object parsedValue;
    if (operator == Op.In || operator == Op.NotIn) {
      // For IN or NOT IN operator
      // We need to split the value into tokens and parse to target field type
      String[] tokens = value.split(",");
      parsedValue = new ArrayList<>(tokens.length);
      for (int i = 0; i < tokens.length; i++) {
        String token = tokens[i];
        Object itemParsedValue = parseValue(field, operator, token);
        ((List<Object>) parsedValue).add(itemParsedValue);
        String property = String.format("%s_%s_%d_%d", field.getTableName(), field.getFieldName(),
          index, i);
        ParameterMapping parameterMapping = new ParameterMapping.Builder(
          ms.getConfiguration(),
          property,
          fieldType).build();
        parameterMappings.add(parameterMapping);
        additionalParameters.put(property, itemParsedValue);
      }
      String property = String.format("%s_%s_%d", field.getTableName(), field.getFieldName(),
        index);
      parameterObject.put(property, parsedValue);
    } else {
      // Single value case
      parsedValue = parseValue(field, operator, value);
      String property = String.format("%s_%s_%d", field.getTableName(), field.getFieldName(),
        index);
      ParameterMapping parameterMapping = new ParameterMapping.Builder(
        ms.getConfiguration(),
        property,
        fieldType).build();
      parameterMappings.add(parameterMapping);
      parameterObject.put(property, parsedValue);
    }

    return new WhereClause(field.getTableName(), field.getFieldName(), operator, parsedValue,
      fieldType);
  }

  /**
   * Convert string value into target field type
   *
   * @param field    query mapping field
   * @param operator djolar operator
   * @param value    source string value
   * @return converted target value for given field type
   */
  private Object parseValue(QueryMapping.Item field, Op operator, String value) {
    if (field.getFieldType().isPrimitive()) {
      switch (field.getFieldType().getName()) {
        case "int":
          return Integer.parseInt(value);
        case "boolean":
          return Boolean.parseBoolean(value);
        case "long":
          return Long.parseLong(value);
        case "float":
          return Float.parseFloat(value);
        case "double":
          return Double.parseDouble(value);
        case "short":
          return Short.parseShort(value);
        default:
          // unsupported primitive type
          throw new DjolarParserException("unsupported primitive type");
      }
    } else if (field.getFieldType().equals(String.class)) {
      if (operator == Op.Contain || operator == Op.IgnoreCaseContain) {
        return String.format("%%%s%%", value);
      } else if (operator == Op.StartsWith) {
        return String.format("%s%%", value);
      } else if (operator == Op.EndsWith) {
        return String.format("%%%s", value);
      } else {
        return value;
      }
    } else if (field.getFieldType().equals(Integer.class)) {
      return Integer.parseInt(value);
    } else if (field.getFieldType().equals(Boolean.class)) {
      return Boolean.parseBoolean(value);
    } else if (field.getFieldType().equals(Long.class)) {
      return Long.parseLong(value);
    } else if (field.getFieldType().equals(Float.class)) {
      return Float.parseFloat(value);
    } else if (field.getFieldType().equals(Double.class)) {
      return Double.parseDouble(value);
    } else if (field.getFieldType().equals(Short.class)) {
      return Short.parseShort(value);
    } else {
      return null;
    }
  }

  /**
   * Parse order by clause
   *
   * @param orderBy order by clause
   * @return order by statements
   */
  private List<String> parseOrderByFields(String orderBy) {
    if (orderBy == null) {
      return null;
    }
    orderBy = orderBy.trim();
    if (orderBy.length() == 0) {
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
