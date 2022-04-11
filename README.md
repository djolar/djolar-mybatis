# Djolar for MyBatis

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
