package com.enixyu.djolar.mybatis.plugin;

import com.enixyu.djolar.mybatis.mapper.BlogMapper;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.session.SqlSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public abstract class BaseBenchmark extends SessionAwareManager {

  protected abstract String getBenchmarkInclude();

  @Fork(1)
  @Threads(4)
  @Benchmark
  @Warmup(iterations = 2)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @BenchmarkMode(Mode.Throughput)
  public void benchmarkDjolarIntegerEqual(Blackhole blackhole) {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      request.setQuery("id__eq__1");
      blackhole.consume(mapper.findAll(request));
    }
  }

  @Fork(1)
  @Threads(4)
  @Benchmark
  @Warmup(iterations = 2)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @BenchmarkMode(Mode.Throughput)
  public void benchmarkIntegerEqualBaseline(Blackhole blackhole) {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      blackhole.consume(mapper.findById(1));
    }
  }
}
