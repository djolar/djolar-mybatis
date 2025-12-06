package com.enixyu.djolar.mybatis.mapper;

import com.enixyu.djolar.mybatis.annotation.Mapping;
import com.enixyu.djolar.mybatis.domain.Blog;
import com.enixyu.djolar.mybatis.mapping.BlogQueryMappingWithoutTableAnnotation;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Mapping(BlogQueryMappingWithoutTableAnnotation.class)
public interface BlogMapperWithoutTableAnnotation {

  List<Blog> findAll(QueryRequest queryRequest);
}
