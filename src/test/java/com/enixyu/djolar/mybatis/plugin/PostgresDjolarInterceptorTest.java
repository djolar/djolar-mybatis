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

import com.enixyu.djolar.mybatis.mapper.BlogMapper;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PostgresDjolarInterceptorTest extends BaseTest {

  @Test
  void testJsonOverlapsShouldFail() {
    Assertions.assertThrows(PersistenceException.class, () -> {
      try (SqlSession session = this.sessionFactory.openSession()) {
        setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
          DjolarProperty.VALUE_ON);
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("tags__jo__1,2,3");
        mapper.findAll(queryRequest);
      }
    });
  }

  @Override
  protected String getMybatisConfigFileName() {
    return "mybatis-config-postgres.xml";
  }
}
