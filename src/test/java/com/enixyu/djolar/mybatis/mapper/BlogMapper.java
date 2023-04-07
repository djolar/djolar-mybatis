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
package com.enixyu.djolar.mybatis.mapper;

import com.enixyu.djolar.mybatis.annotation.AdditionalSort;
import com.enixyu.djolar.mybatis.annotation.AdditionalWhere;
import com.enixyu.djolar.mybatis.annotation.Mapping;
import com.enixyu.djolar.mybatis.domain.Blog;
import com.enixyu.djolar.mybatis.mapping.BlogDjolarMapping;
import com.enixyu.djolar.mybatis.parser.QueryRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@Mapping(Blog.class)
public interface BlogMapper {

  List<Blog> findAll(QueryRequest request);

  @AdditionalWhere(where = "user_id__eq__1")
  @AdditionalSort(sort = "-id")
  List<Blog> findMyBlogs(QueryRequest request);

  @AdditionalWhere(where = "user_id__eq__1")
  @AdditionalSort(sort = "-id")
  List<Blog> findMyBlogs(QueryRequest request, int i, Long j);

  Blog findById(int id);

  @Mapping(BlogDjolarMapping.class)
  List<Blog> findBlogWithUser(QueryRequest request);

  List<Blog> findBlogWithIdRange(@Param("ids1") List<Integer> ids1,
    @Param("ids2") List<Integer> ids2);
}
