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
import com.enixyu.djolar.mybatis.mapper.BlogMapper;
import com.enixyu.djolar.mybatis.parser.Op;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import com.enixyu.djolar.mybatis.parser.QueryRequest.QueryRequestBuilder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
public abstract class BaseSessionTest {

    protected SqlSessionFactory sessionFactory;

    protected abstract String getMybatisConfigFileName();

    protected abstract String getBenchmarkInclude();

    public BaseSessionTest() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(getMybatisConfigFileName());
            this.sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            Assertions.assertEquals(6, results.size());
        }
    }

    @Test
    void testIntegerGreaterThanAndEqual() {
        try (SqlSession session = this.sessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            QueryRequest request = new QueryRequest();
            request.setQuery("id__ge__8");
            List<Blog> results = mapper.findAll(request);
            Assertions.assertEquals(7, results.size());
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
            .addQuery("col1", Op.Equal, "1")
            .addQuery("col2", Op.LessThan, "2")
            .addQuery("col3", Op.LessThanOrEqual, "3")
            .addQuery("col4", Op.GreaterThan, "4")
            .addQuery("col5", Op.GreaterThanOrEqual, "5")
            .addQuery("col6", Op.Contain, "6")
            .addQuery("col7", Op.StartsWith, "7")
            .addQuery("col8", Op.EndsWith, "8")
            .build();

        Assertions.assertEquals(
            "col1__eq__1|col2__lt__2|col3__le__3|col4__gt__4|col5__ge__5|col6__co__6|col7__sw__7|col8__ew__8",
            queryRequest.getQuery());
    }

    @Test
    protected void launchBenchmark() throws RunnerException {
        Options options = new OptionsBuilder()
            .include(getBenchmarkInclude())
            .timeUnit(TimeUnit.SECONDS)
            .warmupTime(TimeValue.seconds(2))
            .threads(4)
            .warmupIterations(2)
            .measurementIterations(5)
            .measurementTime(TimeValue.seconds(10))
            .forks(1)
            .build();
        new Runner(options).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkDjolarIntegerEqual(Blackhole blackhole) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            QueryRequest request = new QueryRequest();
            request.setQuery("id__eq__1");
            blackhole.consume(mapper.findAll(request));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkIntegerEqualBaseline(Blackhole blackhole) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            blackhole.consume(mapper.findById(1));
        }
    }
}
