package com.enixyu.djolar.mybatis.plugin;

import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public abstract class SessionAwareManager {

  protected SqlSessionFactory sessionFactory;

  protected abstract String getMybatisConfigFileName();

  public SessionAwareManager() {
    try {
      InputStream inputStream = Resources.getResourceAsStream(getMybatisConfigFileName());
      this.sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
