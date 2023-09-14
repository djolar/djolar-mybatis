package com.enixyu.djolar.mybatis.parser;

public class OrderClause extends Clause {

  private boolean ascending;

  public OrderClause(String tableName, String columnName, boolean ascending, boolean needEscape) {
    super(tableName, columnName, needEscape);
    this.ascending = ascending;
  }

  public boolean isAscending() {
    return ascending;
  }

  public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }

  @Override
  public String toString() {
    return "OrderClause{" +
      "tableName='" + getTableName() + '\'' +
      ", columnName='" + getColumnName() + '\'' +
      ", ascending=" + ascending + '\'' +
      ", needEscape=" + isNeedEscape() +
      '}';
  }
}
