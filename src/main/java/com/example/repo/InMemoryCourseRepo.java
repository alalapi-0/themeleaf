package com.example.repo; // 仓库类所在包

import com.example.model.Course; // 引入课程实体

import java.util.ArrayList; // 使用可变列表初始化种子数据
import java.util.Collections; // 用于创建不可变集合
import java.util.List; // 列表接口
import java.util.Optional; // 用 Optional 封装查询结果

public class InMemoryCourseRepo { // 内存中的课程仓库
    private static final InMemoryCourseRepo INSTANCE = new InMemoryCourseRepo(); // 单例实例

    private final List<Course> courses; // 不可变课程列表

    private InMemoryCourseRepo() { // 私有构造函数加载种子数据
        List<Course> seed = new ArrayList<>(); // 创建可变列表用于填充数据
        seed.add(new Course(1, "Java 入门实战", "BEGINNER", 12, "USER",
                "面向零基础学习者的 Java 基础课程，涵盖语法与常用类库。")); // 初级课程示例
        seed.add(new Course(2, "企业级 Servlet 开发", "ADVANCED", 18, "USER",
                "深入理解 Servlet 3.x 特性，构建可扩展的 Web 应用。")); // 高级用户课程
        seed.add(new Course(3, "安全加固与运维", "ADVANCED", 21, "ADMIN",
                "针对高级运维与安全策略的管理员专享课程。")); // 管理员专享课程
        seed.add(new Course(4, "前端整合基础", "BEGINNER", 10, "USER",
                "学习 HTML/CSS/JS 与后端模板引擎的协作流程。")); // 面向青少年的基础课程
        seed.add(new Course(5, "架构设计工作坊", "ADVANCED", 25, "ADMIN",
                "以实战案例讲解高可用架构与团队协作流程。")); // 高阶架构课程
        this.courses = Collections.unmodifiableList(seed); // 转换为不可修改列表
    }

    public static InMemoryCourseRepo getInstance() { // 提供单例访问入口
        return INSTANCE; // 返回全局实例
    }

    public List<Course> findAll() { // 查询全部课程
        return courses; // 返回不可变列表
    }

    public Optional<Course> findById(int id) { // 按编号查找课程
        return courses.stream().filter(c -> c.getId() == id).findFirst(); // 使用流匹配并返回 Optional

    }
}
