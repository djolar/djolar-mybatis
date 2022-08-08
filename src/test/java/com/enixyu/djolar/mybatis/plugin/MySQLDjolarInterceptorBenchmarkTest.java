package com.enixyu.djolar.mybatis.plugin;

public class MySQLDjolarInterceptorBenchmarkTest extends BaseBenchmark {

  @Override
  protected String getMybatisConfigFileName() {
    return "mybatis-config-mysql.xml";
  }

  @Override
  protected String getBenchmarkInclude() {
    return MySQLDjolarInterceptorBenchmarkTest.class.getSimpleName();
  }
}
