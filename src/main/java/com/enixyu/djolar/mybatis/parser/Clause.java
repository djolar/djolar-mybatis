package com.enixyu.djolar.mybatis.parser;

public abstract class Clause {

  private final String databaseName;

  private final String tableName;

  private String columnName;

  private final String jsonPath;

  private final boolean needEscape;

  public Clause(String databaseName, String tableName, String columnName, boolean needEscape,
    String jsonPath) {
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.columnName = columnName;
    this.needEscape = needEscape;
    this.jsonPath = jsonPath;
  }

  public boolean isNeedEscape() {
    return needEscape;
  }

  public String getTableName() {
    return tableName;
  }

  public boolean isTableNameNotExist() {
    return tableName == null || tableName.isEmpty();
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public boolean isDatabaseNameNotExist() {
    return databaseName == null || databaseName.isEmpty();
  }

  public String getJsonPath() {
    return jsonPath;
  }
}
