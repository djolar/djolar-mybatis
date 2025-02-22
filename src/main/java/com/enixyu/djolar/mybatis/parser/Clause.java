package com.enixyu.djolar.mybatis.parser;

public abstract class Clause {

  private String databaseName;

  private String tableName;

  private String columnName;

  private boolean needEscape;

  public Clause(String databaseName, String tableName, String columnName, boolean needEscape) {
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.columnName = columnName;
    this.needEscape = needEscape;
  }

  public boolean isNeedEscape() {
    return needEscape;
  }

  public String getTableName() {
    return tableName;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setNeedEscape(boolean needEscape) {
    this.needEscape = needEscape;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }
}
