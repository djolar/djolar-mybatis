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

import org.apache.ibatis.mapping.BoundSql;

public class ParseResult {

  private BoundSql boundSql;
  private Object parameter;

  public ParseResult(BoundSql boundSql, Object parameter) {
    this.boundSql = boundSql;
    this.parameter = parameter;
  }

  public BoundSql getBoundSql() {
    return boundSql;
  }

  public void setBoundSql(BoundSql boundSql) {
    this.boundSql = boundSql;
  }

  public Object getParameter() {
    return parameter;
  }

  public void setParameter(Object parameter) {
    this.parameter = parameter;
  }
}
