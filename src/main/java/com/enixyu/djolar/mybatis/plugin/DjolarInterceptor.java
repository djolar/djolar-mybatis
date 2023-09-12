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
package com.enixyu.djolar.mybatis.plugin;

import com.enixyu.djolar.mybatis.dialect.DjolarAutoDialect;
import com.enixyu.djolar.mybatis.parser.DjolarParser;
import com.enixyu.djolar.mybatis.parser.ParseResult;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({@Signature(
  type = Executor.class,
  method = "query",
  args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
  type = Executor.class,
  method = "query",
  args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
    BoundSql.class}
)})
public class DjolarInterceptor implements Interceptor {

  private final DjolarParser parser;
  private final DjolarAutoDialect djolarAutoDialect;

  public DjolarInterceptor() {
    djolarAutoDialect = new DjolarAutoDialect();
    parser = new DjolarParser(djolarAutoDialect);
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    Executor executor = (Executor) invocation.getTarget();
    MappedStatement ms = (MappedStatement) args[0];
    Object parameter = args[1];
    RowBounds rowBounds = (RowBounds) args[2];
    ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];
    BoundSql boundSql = args.length > 4 ? (BoundSql) args[5] : ms.getBoundSql(parameter);

    if (parameter == null) {
      return invocation.proceed();
    }

    ParseResult result = parser.parse(ms, boundSql, parameter);
    if (result == null) {
      return invocation.proceed();
    }

    CacheKey cacheKey = executor.createCacheKey(ms, result.getParameter(), rowBounds,
      result.getBoundSql());
    return executor.query(ms, result.getParameter(), rowBounds, resultHandler, cacheKey,
      result.getBoundSql());
  }

  @Override
  public void setProperties(Properties properties) {
    djolarAutoDialect.initAutoDialect(properties);
    parser.setThrowIfFieldNotFound(
      properties.getProperty(DjolarProperty.KEY_THROW_IF_FIELD_NOT_FOUND, DjolarProperty.VALUE_OFF)
        .equals(DjolarProperty.VALUE_ON));
    parser.setThrowIfOperatorNotSupport(
      properties.getProperty(DjolarProperty.KEY_THROW_IF_OPERATOR_NOT_SUPPORT,
        DjolarProperty.VALUE_OFF).equals(DjolarProperty.VALUE_ON));
    parser.setThrowIfExpressionInvalid(
      properties.getProperty(DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
        DjolarProperty.VALUE_OFF).equals(DjolarProperty.VALUE_ON));
  }
}
