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
package com.enixyu.djolar.mybatis.dialect;

import com.enixyu.djolar.mybatis.parser.Op;
import com.enixyu.djolar.mybatis.parser.OrderClause;
import com.enixyu.djolar.mybatis.parser.QueryMapping.Item;
import com.enixyu.djolar.mybatis.parser.WhereClause;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

public class PostgreSQLDialect extends BaseDialect {

  @Override
  public String buildWhere(WhereClause whereClause) {
    switch (whereClause.getOperator()) {
      case IS_NULL:
      case IS_NOT_NULL:
        return String.format("%s %s", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
      case IGNORE_CASE_CONTAIN:
        return String.format("LOWER(%s) %s LOWER(?)", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
      case IN:
      case NOT_IN: {
        Object inVal = whereClause.getValue();
        if (!(inVal instanceof List)) {
          throw new IllegalArgumentException("IN value should be a valid list");
        }
        String mark = ((List<?>) inVal)
          .stream()
          .map(ignore -> "?")
          .collect(Collectors.joining(","));
        return String.format("%s %s (%s)", getColumnName(whereClause),
          whereClause.getOperator().getSymbol(),
          mark
        );
      }
      case JSON_OVERLAPS:
        throw new IllegalArgumentException("jo (json overlaps) is not support in postgresql");
      case JSON_CONTAINS:
        return String.format("%s @> CAST(? AS JSONB)", getColumnName(whereClause));
      default:
        return String.format("%s %s ?", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
    }
  }

  @Override
  public Object parseQueryFieldValue(MappedStatement ms, List<ParameterMapping> parameterMappings,
    Map<String, Object> parameterObject, Map<String, Object> additionalParameters,
    int fieldIndex, String fieldName, Op op, Item field, String value) {
    if (op == Op.IN || op == Op.NOT_IN) {
      // For IN or NOT IN operator
      // We need to split the value into tokens and parse to target field type
      return parseListValueField(ms, parameterMappings, parameterObject, additionalParameters,
        fieldIndex,
        op, field, value);
    } else if (op == Op.JSON_OVERLAPS) {
      // json overlaps
      throw new IllegalArgumentException("jo (json overlaps) is not support in postgresql");
    } else if (op == Op.JSON_CONTAINS) {
      // json contains
      return parseSingleValueField(ms, parameterMappings, parameterObject, fieldIndex, op, field,
        "[" + value + "]");
    } else {
      // Single value case
      return parseSingleValueField(ms, parameterMappings, parameterObject, fieldIndex, op, field,
        value);
    }
  }

  @Override
  public String buildOrderBy(OrderClause orderClause) {
    return String.format("%s %s", getColumnName(orderClause),
      orderClause.isAscending() ? "ASC" : "DESC"
    );
  }

  @Override
  protected String getFieldQuoteSymbol() {
    return "\"";
  }
}
