package com.enixyu.djolar.mybatis.dialect;

import com.enixyu.djolar.mybatis.parser.Clause;

public abstract class BaseDialect implements Dialect {

  protected abstract String getFieldQuoteSymbol();

  protected String getColumnName(Clause clause) {
    String quote = clause.isNeedEscape() ? getFieldQuoteSymbol() : "";
    return clause.getTableName() == null
      ? String.format("%s%s%s", quote, clause.getColumnName(), quote)
      : String.format("%s%s%s.%s%s%s", quote, clause.getTableName(), quote, quote,
        clause.getColumnName(), quote);
  }
}
