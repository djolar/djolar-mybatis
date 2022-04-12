# Djolar for MyBatis

`Djolar` is a protocol like graphql, but only focus on how to filter data in database. It is very common to use multiple filter in web-app or mobile-app, `Djolar` is lifesaver to build clean API services, unified frontend component. 

For example, we need to search a user with 
* `name` equal to `enix`, 
* `age` greater than `18` 

so we can invoke the api with the following query parameters:

```text
http://localhost:8000/api/v1/users?q=name__eq__enix|age__gt__18
```

`q` stand for `query`, the value syntax as below:

```text
value := <filter-clause1>[|<filter-clause2>|...] 
filter-clause := <field_name>__<operator>__value
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
    <plugin interceptor="com.enixyu.djolar.mybatis.plugin.DjolarInterceptor" />
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

# 3. Djolar implementation for other language

* [djolar for golang](https://github.com/enix223/go-djolar)
* [djolar for python django](https://github.com/enix223/djolar)