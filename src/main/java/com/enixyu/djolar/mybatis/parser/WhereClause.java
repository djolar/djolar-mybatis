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

public class WhereClause extends Clause {

  private Op operator;
  private Object value;
  private Class<?> valueType;

  public WhereClause(String databaseName, String tableName, String columnName, Op operator,
    Object value, Class<?> valueType, boolean needEscape) {
    super(databaseName, tableName, columnName, needEscape);
    this.operator = operator;
    this.value = value;
    this.valueType = valueType;
  }

  public Class<?> getValueType() {
    return valueType;
  }

  public void setValueType(Class<?> valueType) {
    this.valueType = valueType;
  }

  public Op getOperator() {
    return operator;
  }

  public void setOperator(Op operator) {
    this.operator = operator;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "WhereClause{" +
      "tableName='" + getTableName() + '\'' +
      ", columnName='" + getColumnName() + '\'' +
      ", operator=" + operator +
      ", value=" + value +
      ", valueType=" + valueType +
      ", needEscape=" + isNeedEscape() +
      '}';
  }
}
