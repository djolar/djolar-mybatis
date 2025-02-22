package com.enixyu.djolar.mybatis.mapper;

import com.enixyu.djolar.mybatis.annotation.Mapping;
import com.enixyu.djolar.mybatis.domain.Rate;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Mapping(Rate.class)
public interface RateMapper {

  List<Rate> find(QueryRequest request);
}
