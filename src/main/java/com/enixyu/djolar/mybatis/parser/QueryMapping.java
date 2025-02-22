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

import java.util.HashMap;
import java.util.Map;

public class QueryMapping {

  Map<String, Item> fieldDefinitions;

  public QueryMapping() {
    fieldDefinitions = new HashMap<>();
  }

  public Item get(String name) {
    return fieldDefinitions.get(name);
  }

  public void set(String name, Item item) {
    fieldDefinitions.put(name, item);
  }

  public static class Item {

    private String databaseName;
    private String tableName;
    private String fieldName;
    private Class<?> fieldType;

    public String getDatabaseName() {
      return databaseName;
    }

    public void setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
    }

    public String getTableName() {
      return tableName;
    }

    public void setTableName(String tableName) {
      this.tableName = tableName;
    }

    public String getFieldName() {
      return fieldName;
    }

    public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
    }

    public Class<?> getFieldType() {
      return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
      this.fieldType = fieldType;
    }

    public Item(String databaseName, String tableName, String fieldName, Class<?> fieldType) {
      this.databaseName = databaseName;
      this.tableName = tableName;
      this.fieldName = fieldName;
      this.fieldType = fieldType;
    }
  }
}
