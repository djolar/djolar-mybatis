package com.enixyu.djolar.mybatis.domain;

import com.enixyu.djolar.mybatis.annotation.Table;

@Table(value = "rate", databaseName = "djolar")
public class Rate {
  private int id;

  private int level;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
