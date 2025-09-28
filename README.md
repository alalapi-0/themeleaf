# Themeleaf Demo

使用 Java 8、Servlet 3.1 与 Thymeleaf 3 构建的最小演示站点，提供课程列表与详情浏览。

## 运行

```bash
mvn -DskipTests jetty:run
```

启动后可通过以下地址访问：

- http://localhost:8080/
- http://localhost:8080/tags
- http://localhost:8080/courses
- http://localhost:8080/courses/detail?id=1

## 功能

- 使用内存仓库提供多条课程数据（含管理员专享、不同年龄限制）。
- 课程列表支持关键字搜索与分页参数控制。
- 课程详情提供成人向与管理员提示，并在级别间切换文案。
- Header 片段演示 Thymeleaf 模板复用。
