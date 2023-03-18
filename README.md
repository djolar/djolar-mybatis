# Djolar for MyBatis

## Introduction

`Djolar` is a protocol like graphql, but only focus on how to filter data in database. It is very
common to use multiple filter in web-app or mobile-app, `Djolar` is lifesaver to build clean API
services, unified frontend component.

For example, we need to search a user with

* `name` equal to `enix`,
* `age` greater than `18`

so we can invoke the api with the following query parameters:

```text
http://localhost:8000/api/v1/users?q=name__eq__enix|age__gt__18
```

`q` stand for `query`, the query value syntax as below:

```text
q-value := <filter-clause1>[|<filter-clause2>|...] 
filter-clause := <field-name>__<operator>__<filter-value>
```

support operators:

| operator | stands for                 |
|----------|----------------------------|
| eq       | equal (=)                  |
| gt       | greater than (>)           |
| ge       | greater than or equal (>=) |
| lt       | less than (<)              |
| le       | less than or equal (<=)    |
| co       | contain (LIKE)             |
| sw       | starts with (LIKE)         |
| ew       | ends with (LIKE)           |
| in       | in (IN)                    |
| nn       | is not null                |
| nu       | is null                    |

## 1. Installation

```xml

<dependency>
  <groupId>com.enixyu</groupId>
  <artifactId>djolar-mybatis</artifactId>
  <version>${latest.version}</version>
</dependency>
```

## 2. Usage

1. Setup interceptor for mybatis

```xml

<plugins>
  <!-- other interceptors -->
  ...
  <plugin interceptor="com.enixyu.djolar.mybatis.plugin.DjolarInterceptor"/>
</plugins>
```

2. Define mapper interface

```java

@Mapper
// Specify the mapping class
@Mapping(Blog.class)
public interface BlogMapper {

  // DjolarInterceptor only handle method with QueryRequest as parameter
  List<Blog> findAll(QueryRequest request);

  Blog findById(int id);

  // Override the mapping class defined in class level
  @Mapping(BlogDjolarMapping.class)
  List<Blog> findBlogWithUser(QueryRequest request);
}
```

3. Define the mapper xml

```xml

<mapper namespace="com.enixyu.djolar.mybatis.mapper.BlogMapper">
  ...

  <select id="findAll" resultType="com.enixyu.djolar.mybatis.domain.Blog">
    SELECT * FROM `blog`
  </select>

  <select id="findBlogWithUser" resultType="com.enixyu.djolar.mybatis.domain.Blog">
    SELECT `blog`.* FROM `blog`
    INNER JOIN `user` ON `blog`.`user_id` = `user`.`id`
  </select>

  ...
</mapper>
```

4. Client make query

```java
public class Test {

  @Test
  public void testDjolarIntegerEqual() {
    try (SqlSession session = this.sessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      QueryRequest request = new QueryRequest();
      // Get the records with `id` column equal to `1`
      request.setQuery("id__eq__1");
      List<Blog> results = mapper.findAll(request);
      Assertions.assertEquals(1, results.size());
    }
  }
}
```

## Release Note

## v1.6.1

* Throw `DjolarParserException` if sort field not found in query mapping.

## v1.6.0

* Throw `DjolarParserException` if query field not found in query mapping
* Throw `DjolarParserException` if operator is not supported

## v1.4.1

* Fix parser failed to parse value when mapping class using boxed type
* Reorganize the benchmark codes
* Upgrade maven version

## v1.4.0

* Support `in` and `not in` operators

  ```java
  public class Test {
    @Test
    void testCollectionIn() {
      try (SqlSession session = this.sessionFactory.openSession()) {
        BlogMapper mapper = session.getMapper(BlogMapper.class);
        QueryRequest request = new QueryRequest();
        // in operator
        request.setQuery("id__in__1,2,3");
        List<Blog> results = mapper.findAll(request);
        Assertions.assertEquals(2, results.size());
  
        // not in operator
        request.setQuery("id__ni__1,2,3");
        List<Blog> results = mapper.findAll(request);
        Assertions.assertEquals(2, results.size());
      }
    }
  }
  ```
* Fix issue when query with multiple same db field, e.g., two filter statement with `id` field int
  the following query clause
  ```text
  id__gt__1|n__sw__abc|id__lt__11
  ```

## v1.3.0

* add `is null` and `is not null` operators
    ```java
    public class Test {
      void test() {
        // filter with blog name is not null
        QueryRequest request = new QueryRequest();
        request.setQuery("blog_name__nn");
        List<Blog> results = mapper.findBlogWithUser(request);
        
        // filter with blog name is null
        request.setQuery("blog_name__nu");
        List<Blog> results = mapper.findBlogWithUser(request);
      }
    } 
    ```

## v1.2.0

* support additional filter and sort in mapper method
    ```java
    @Mapper
    @Mapping(Blog.class)
    public interface BlogMapper {
      // force to filter user_id equal to 1 
      @AdditionalWhere(where = "user_id__eq__1")
      // force to sort by "id" desc and "name" asc
      @AdditionalSort(sort = "-id,name")
      List<Blog> findMyBlogs(QueryRequest request);
    }
    ```

## v1.1.0

* add request field builder

## v1.0.0

* implement djolar spec

## Djolar implementation for other language

* [djolar for golang](https://github.com/enix223/go-djolar)
* [djolar for python django](https://github.com/enix223/djolar)