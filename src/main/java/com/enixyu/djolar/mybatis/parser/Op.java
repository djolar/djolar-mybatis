/*
 * Copyright (c) 2022, enix223@163.com
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * </p>
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * </p>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * </p>
 */
package com.enixyu.djolar.mybatis.parser;

public enum Op {
  Equal("eq", "=", (byte) 1),
  NotEqual("ne", "<>", (byte) 1),
  LessThan("lt", "<", (byte) 1),
  LessThanOrEqual("le", "<=", (byte) 1),
  GreaterThan("gt", ">", (byte) 1),
  GreaterThanOrEqual("ge", ">=", (byte) 1),
  Contain("co", "LIKE", (byte) 1),
  StartsWith("sw", "LIKE", (byte) 1),
  EndsWith("ew", "LIKE", (byte) 1),
  IgnoreCaseContain("ico", "LIKE", (byte) 1),
  In("in", "IN", (byte) 1),
  NotIn("ni", "NOT IN", (byte) 1),
  IsNull("nu", "IS NULL", (byte) 0),
  IsNotNull("nn", "IS NOT NULL", (byte) 0);

  private final String value;
  private final String symbol;
  private final byte numOfOperands;

  Op(String value, String symbol, byte numOfOperands) {
    this.value = value;
    this.symbol = symbol;
    this.numOfOperands = numOfOperands;
  }

  public String getValue() {
    return value;
  }

  public String getSymbol() {
    return symbol;
  }

  public byte getNumOfOperands() {
    return numOfOperands;
  }

  public static Op fromString(String value) {
    for (Op op : Op.values()) {
      if (op.value.equals(value)) {
        return op;
      }
    }

    return null;
  }
}
