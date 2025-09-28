package com.example.repo;

import com.example.model.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InMemoryCourseRepo {
    private static final InMemoryCourseRepo INSTANCE = new InMemoryCourseRepo();

    private final List<Course> courses;

    private InMemoryCourseRepo() {
        List<Course> seed = new ArrayList<>();
        seed.add(new Course(1, "Java 入门实战", "BEGINNER", 12, "USER",
                "面向零基础学习者的 Java 基础课程，涵盖语法与常用类库。"));
        seed.add(new Course(2, "企业级 Servlet 开发", "ADVANCED", 18, "USER",
                "深入理解 Servlet 3.x 特性，构建可扩展的 Web 应用。"));
        seed.add(new Course(3, "安全加固与运维", "ADVANCED", 21, "ADMIN",
                "针对高级运维与安全策略的管理员专享课程。"));
        seed.add(new Course(4, "前端整合基础", "BEGINNER", 10, "USER",
                "学习 HTML/CSS/JS 与后端模板引擎的协作流程。"));
        seed.add(new Course(5, "架构设计工作坊", "ADVANCED", 25, "ADMIN",
                "以实战案例讲解高可用架构与团队协作流程。"));
        this.courses = Collections.unmodifiableList(seed);
    }

    public static InMemoryCourseRepo getInstance() {
        return INSTANCE;
    }

    public List<Course> findAll() {
        return courses;
    }

    public Optional<Course> findById(int id) {
        return courses.stream().filter(c -> c.getId() == id).findFirst();
    }
}
