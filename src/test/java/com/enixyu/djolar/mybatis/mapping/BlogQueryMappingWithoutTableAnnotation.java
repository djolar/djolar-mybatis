package com.enixyu.djolar.mybatis.mapping;

import com.enixyu.djolar.mybatis.annotation.Column;
import java.util.Map;

public class BlogQueryMappingWithoutTableAnnotation {

  private int id;

  @Column(queryAlias = "n")
  private String name;

  @Column(queryAlias = "user_id", columnName = "user_id")
  private int userID;

  private String tags;

  private Map<String, Object> attrs;

  @Column(queryAlias = "meta", columnName = "attrs", jsonPath = "$.meta")
  private String meta;

  @Column(queryAlias = "click", columnName = "attrs", jsonPath = "$.click")
  private int click;
}
