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

import com.enixyu.djolar.mybatis.parser.Clause;
import com.enixyu.djolar.mybatis.parser.Op;
import com.enixyu.djolar.mybatis.parser.OrderClause;
import com.enixyu.djolar.mybatis.parser.QueryMapping.Item;
import com.enixyu.djolar.mybatis.parser.WhereClause;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

public class MySQLDialect extends BaseDialect {

  @Override
  public String buildWhere(WhereClause whereClause) {
    switch (whereClause.getOperator()) {
      case IS_NULL:
      case IS_NOT_NULL:
        return String.format("%s %s", getColumnName(whereClause),
          whereClause.getOperator().getSymbol());
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
        return String.format("JSON_OVERLAPS(%s, ?)", getColumnName(whereClause));
      case JSON_CONTAINS:
        return String.format("JSON_CONTAINS(%s, ?, '$')", getColumnName(whereClause));
      default:
        return String.format("%s %s ?", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
    }
  }

  @Override
  protected String getColumnName(Clause clause) {
    String quote = clause.isNeedEscape() ? getFieldQuoteSymbol() : "";
    return clause.getTableName() == null
      ? String.format("%s%s%s", quote, clause.getColumnName(), quote)
      : isBlank(clause.getDatabaseName())
        ? String.format("%s%s%s.%s%s%s", quote, clause.getTableName(), quote, quote,
        clause.getColumnName(), quote)
        : String.format("%s%s%s.%s%s%s.%s%s%s", quote, clause.getDatabaseName(), quote, quote,
          clause.getTableName(), quote, quote, clause.getColumnName(), quote);
  }

  @Override
  public String buildOrderBy(OrderClause orderClause) {
    return String.format("%s %s", getColumnName(orderClause),
      orderClause.isAscending() ? "ASC" : "DESC"
    );
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
    } else if (op == Op.JSON_OVERLAPS || op == Op.JSON_CONTAINS) {
      // json overlaps/contains
      return parseSingleValueField(ms, parameterMappings, parameterObject, fieldIndex, op, field,
        "[" + value + "]");
    } else {
      // Single value case
      return parseSingleValueField(ms, parameterMappings, parameterObject, fieldIndex, op, field,
        value);
    }
  }

  @Override
  protected String getFieldQuoteSymbol() {
    return "`";
  }
}
