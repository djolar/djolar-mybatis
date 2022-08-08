package com.enixyu.djolar.mybatis.plugin;

public class PostgresDjolarInterceptorBenchmarkTest extends BaseBenchmark {

  @Override
  protected String getMybatisConfigFileName() {
    return "mybatis-config-postgres.xml";
  }

  @Override
  protected String getBenchmarkInclude() {
    return PostgresDjolarInterceptorBenchmarkTest.class.getSimpleName();
  }
}
