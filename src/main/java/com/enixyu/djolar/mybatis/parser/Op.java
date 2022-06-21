/**
 * Copyright (c) 2022, enix223@163.com
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.enixyu.djolar.mybatis.parser;

public enum Op {
    Equal("eq", "="),
    NotEqual("ne", "<>"),
    LessThan("lt", "<"),
    LessThanOrEqual("le", "<="),
    GreaterThan("gt", ">"),
    GreaterThanOrEqual("ge", ">="),
    Contain("co", "LIKE"),
    StartsWith("sw", "LIKE"),
    EndsWith("ew", "LIKE"),
    IgnoreCaseContain("ico", "LIKE"),
    In("in", "IN"),
    NotIn("ni", "NOT IN"),
    IsNull("nu", "IS NULL"),
    IsNotNull("nn", "IS NOT NULL");

    private final String value;
    private final String symbol;

    Op(String value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public String getValue() {
        return value;
    }

    public String getSymbol() {
        return symbol;
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
