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

import java.util.LinkedList;
import java.util.List;

public class QueryRequest {

  private String query;

  private String sort;

  private String group;

  private String having;

  private int limit;

  private int offset;

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getHaving() {
    return having;
  }

  public void setHaving(String having) {
    this.having = having;
  }

  public static class QueryRequestBuilder {

    private final List<String> query;

    private QueryRequestBuilder() {
      query = new LinkedList<>();
    }

    public static QueryRequestBuilder newBuilder() {
      return new QueryRequestBuilder();
    }

    public QueryRequestBuilder addQuery(String columnName, Op op, String value) {
      byte numOfOperands = op.getNumOfOperands();
      if (numOfOperands == 0) {
        query.add(String.format("%s__%s", columnName, op.getValue()));
      } else {
        query.add(String.format("%s__%s__%s", columnName, op.getValue(), value));
      }
      return this;
    }

    public QueryRequest build() {
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery(String.join("|", query));
      return queryRequest;
    }
  }
}
