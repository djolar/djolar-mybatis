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

import com.enixyu.djolar.mybatis.parser.OrderClause;
import com.enixyu.djolar.mybatis.parser.WhereClause;
import java.util.List;
import java.util.stream.Collectors;

public class PostgreSQLDialect extends BaseDialect {

  @Override
  public String buildWhere(WhereClause whereClause) {
    switch (whereClause.getOperator()) {
      case IsNull:
      case IsNotNull:
        return String.format("%s %s", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
      case IgnoreCaseContain:
        return String.format("LOWER(%s) %s LOWER(?)", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
      case In:
      case NotIn: {
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
      default:
        return String.format("%s %s ?", getColumnName(whereClause),
          whereClause.getOperator().getSymbol()
        );
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
