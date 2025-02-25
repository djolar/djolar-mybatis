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

import com.enixyu.djolar.mybatis.domain.Blog;
import com.enixyu.djolar.mybatis.domain.BlogQueryRequest;
import com.enixyu.djolar.mybatis.domain.Rate;
import com.enixyu.djolar.mybatis.domain.UserQueryRequest;
import com.enixyu.djolar.mybatis.exceptions.DjolarParserException;
import com.enixyu.djolar.mybatis.mapper.BlogMapper;
import com.enixyu.djolar.mybatis.mapper.RateMapper;
import com.enixyu.djolar.mybatis.parser.Op;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import com.enixyu.djolar.mybatis.parser.QueryRequest.QueryRequestBuilder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class BaseTest extends SessionAwareManager {

  @Test
  public void testDjolarIntegerEqual() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__eq__1");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(1, results.size());
    }
  }

  @Test
  public void testIntegerGreaterThan() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__gt__8");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(7, results.size());
    }
  }

  @Test
  void testIntegerGreaterThanAndEqual() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__ge__8");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(8, results.size());
    }
  }

  @Test
  void testMultipleSameFieldFilters() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__gt__1|n__sw__abc|id__lt__11");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(8, results.size());
    }
  }

  @Test
  void testCollectionIn() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__in__1,2,3|id__in__2,3");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(2, results.size());
    }
  }

  @Test
  void testEmptyIn() {
    Assertions.assertThrows(PersistenceException.class, () -> {
      try (SqlSession session = this.sessionFactory.openSession()) {
        setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
          DjolarProperty.VALUE_ON);
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest request = new QueryRequest();
        request.setQuery("id__in__");
        mapper.findAll(request);
      }
    });
  }

  @Test
  void testCollectionExclude() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__ni__1,2,3");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(12, results.size());
    }
  }

  @Test
  void testStringContain() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__co__bc");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(10, results.size());
    }
  }

  @Test
  void testStringIgnoreCaseContain() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__ico__BC");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(10, results.size());
    }
  }

  @Test
  void testStringStartsWith() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__sw__abc");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(9, results.size());
    }
  }

  @Test
  void testStringEndsWith() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__ew__c6");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(2, results.size());
    }
  }

  @Test
  void testTwoNameFilter() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("blog_name__eq__abc1|user_name__eq__user1");
      List<Blog> results = mapper.findBlogWithUser(request);
      Assertions.assertEquals(1, results.size());
    }
  }

  @Test
  void testIsNotNull() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("blog_name__nn");
      List<Blog> results = mapper.findBlogWithUser(request);
      Assertions.assertEquals(14, results.size());
    }
  }

  @Test
  void testIsNull() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("blog_name__nu");
      List<Blog> results = mapper.findBlogWithUser(request);
      Assertions.assertEquals(1, results.size());
    }
  }

  @Test
  void testAdditionalWhere() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__eq__abc1");
      List<Blog> results = mapper.findMyBlogs(request);
      Assertions.assertEquals(1, results.size());

      request.setQuery("n__eq__abc4");
      request.setSort(null);
      results = mapper.findMyBlogs(request);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testIntegrateWithPageHelper() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      Page<?> page = PageHelper.startPage(1, 5);
      QueryRequest request = new QueryRequest();
      request.setQuery("n__co__bc");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(5, results.size());
      page.close();
    }
  }

  @Test
  void testQueryRequestBuilder() {
    QueryRequest queryRequest = QueryRequestBuilder.newBuilder()
      .addQuery("col1", Op.EQUAL, "1")
      .addQuery("col2", Op.LESS_THAN, "2")
      .addQuery("col3", Op.LESS_THAN_OR_EQUAL, "3")
      .addQuery("col4", Op.GREATER_THAN, "4")
      .addQuery("col5", Op.GREATER_THAN_OR_EQUAL, "5")
      .addQuery("col6", Op.CONTAIN, "6")
      .addQuery("col7", Op.STARTS_WITH, "7")
      .addQuery("col8", Op.ENDS_WITH, "8")
      .addQuery("col9", Op.IS_NULL, null)
      .addQuery("col10", Op.IS_NOT_NULL, null)
      .build();

    Assertions.assertEquals(
      "col1__eq__1|col2__lt__2|col3__le__3|col4__gt__4|" +
        "col5__ge__5|col6__co__6|col7__sw__7|col8__ew__8|col9__nu|col10__nn",
      queryRequest.getQuery());
  }

  @Test
  void testBoxedType() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("user_id__eq__1");
      List<Blog> results = mapper.findBlogWithUser(request);
      Assertions.assertEquals(3, results.size());
    }
  }

  @Test
  void testQueryFieldNotFoundInMapping() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("no_such_field__eq__1");
      Assertions.assertThrows(DjolarParserException.class, () -> {
        try {
          mapper.findBlogWithUser(request);
        } catch (Exception e) {
          throw e.getCause();
        }
      });
    }
  }

  @Test
  void testUnsupportedOperator() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("user_id__xx__1");
      Assertions.assertThrows(DjolarParserException.class, () -> {
        try {
          mapper.findBlogWithUser(request);
        } catch (Exception e) {
          throw e.getCause();
        }
      });
    }
  }

  @Test
  void testSortFieldNotFoundInMapping() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setSort("-no_such_field");
      Assertions.assertThrows(DjolarParserException.class, () -> {
        try {
          mapper.findBlogWithUser(request);
        } catch (Exception e) {
          throw e.getCause();
        }
      });
    }
  }

  @Test
  void testSelectWithQueryRequestSubclass() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      UserQueryRequest request = new UserQueryRequest(1);
      request.setQuery("n__eq__abc1");
      List<Blog> results = mapper.findByUserQueryRequest(request);
      Assertions.assertEquals(1, results.size());

      request.setUserId(2);
      request.setQuery("n__eq__abc1");
      results = mapper.findByUserQueryRequest(request);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testSelectWithBlogQueryRequest() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      BlogQueryRequest request = new BlogQueryRequest(1);
      request.setQuery("n__eq__abc1");
      List<Blog> results = mapper.findByBlogQueryRequest(request);
      Assertions.assertEquals(1, results.size());

      request.setUserId(2);
      request.setQuery("n__eq__abc1");
      results = mapper.findByBlogQueryRequest(request);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testMultipleParameters() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("n__eq__abc1|id__eq__1");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(1, results.size());

      results = mapper.findUserBlogs(null, 1);
      Assertions.assertEquals(3, results.size());
    }
  }

  @Test
  void testMultipleParametersWithoutQueryRequest() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      List<Blog> results = mapper.findByUserIdAndName(1, "abc1");
      Assertions.assertEquals(1, results.size());
    }
  }

  @Test
  void testEmptyValueShouldReturnNoResult() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("n__eq__");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testNoSuchFieldShouldReturnNoResult() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_FIELD_NOT_FOUND,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("no_such_field__eq__1");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testNoSuchFieldShouldThrow() {
    Assertions.assertThrows(PersistenceException.class, () -> {
      try (SqlSession session = this.sessionFactory.openSession()) {
        setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_FIELD_NOT_FOUND,
          DjolarProperty.VALUE_ON);
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("no_such_field__eq__1");
        List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
        Assertions.assertEquals(0, results.size());
      }
    });
  }

  @Test
  void testInvalidOperatorShouldReturnNoResult() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_OPERATOR_NOT_SUPPORT,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("user_id__xy__1");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testInvalidOperatorShouldThrow() {
    Assertions.assertThrows(PersistenceException.class, () -> {
      try (SqlSession session = this.sessionFactory.openSession()) {
        setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_OPERATOR_NOT_SUPPORT,
          DjolarProperty.VALUE_ON);
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("user_id__xy__1");
        List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
        Assertions.assertEquals(0, results.size());
      }
    });
  }

  @Test
  void testEmptyQueryShouldOK() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(3, results.size());

      queryRequest.setQuery("   ");
      results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(3, results.size());
    }
  }

  @Test
  void testSortOK() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setSort("-user_id");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(3, results.size());
      Assertions.assertEquals("abc3", results.get(0).getName());
    }
  }

  @Test
  void testSortWithEmptyStringShouldOK() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setSort("");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(3, results.size());

      queryRequest.setSort("  ");
      results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(3, results.size());
    }
  }

  @Test
  void testMuteWhenExpressionValueInvalid() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("user_id__eq__ABC");
      List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testInvalidExpressionValueShouldThrow() {
    Assertions.assertThrows(PersistenceException.class, () -> {
      try (SqlSession session = this.sessionFactory.openSession()) {
        setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
          DjolarProperty.VALUE_ON);
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("user_id__eq__ABC");
        List<Blog> results = mapper.findUserBlogs(queryRequest, 1);
        Assertions.assertEquals(0, results.size());
      }
    });
  }

  @Test
  void testJsonContainsShouldSuccess() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("tags__jc__1,2,3");
      List<Blog> results = mapper.findAll(queryRequest);
      Assertions.assertEquals(2, results.size());

      queryRequest.setQuery("tags__jc__1");
      results = mapper.findAll(queryRequest);
      Assertions.assertEquals(8, results.size());

      queryRequest.setQuery("tags__jc__1,4");
      results = mapper.findAll(queryRequest);
      Assertions.assertEquals(0, results.size());

      queryRequest.setQuery("tags__jc__4");
      results = mapper.findAll(queryRequest);
      Assertions.assertEquals(0, results.size());
    }
  }

  @Test
  void testDjolarQueryWithListParams() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      setDjolarParameter(session, DjolarProperty.KEY_THROW_IF_EXPRESSION_INVALID,
        DjolarProperty.VALUE_OFF);
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("n__eq__abc1|user_id__in__1,2");
      List<Integer> ids1 = new ArrayList<>();
      ids1.add(1);
      ids1.add(2);
      ids1.add(3);
      List<Integer> ids2 = new ArrayList<>();
      ids2.add(4);
      ids2.add(5);
      ids2.add(6);
      List<Blog> results = mapper.findBlogWithIdRange(queryRequest, ids1, ids2);
      Assertions.assertEquals(1, results.size());
    }
  }

  @Test
  void testTableAnnotationWithDatabase() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      RateMapper mapper = session.getMapper(RateMapper.class);
      QueryRequest queryRequest = new QueryRequest();
      queryRequest.setQuery("id__eq__1");
      List<Rate> results = mapper.find(queryRequest);
      Assertions.assertEquals(1, results.size());
      Assertions.assertEquals(1, results.get(0).getId());
    }
  }

  protected void setDjolarParameter(SqlSession session, String key, String value) {
    Properties props = new Properties();
    props.setProperty(key, value);
    session.getConfiguration().getInterceptors().stream()
      .filter(i -> i.getClass() == DjolarInterceptor.class)
      .findAny()
      .ifPresent(i -> i.setProperties(props));
  }
}
