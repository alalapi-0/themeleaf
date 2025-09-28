package com.example.model; // 声明课程模型所在的包路径

import java.util.Objects; // 引入 Objects 工具类用于 equals/hashCode

public class Course { // 定义课程实体类
    private final int id; // 课程编号，作为唯一标识
    private final String title; // 课程标题
    private final String level; // 课程难度级别
    private final int minAge; // 建议最低年龄
    private final String requireRole; // 所需角色权限
    private final String description; // 课程简介

    public Course(int id, String title, String level, int minAge, String requireRole, String description) { // 构造方法注入所有字段
        this.id = id; // 保存课程编号
        this.title = title; // 保存标题
        this.level = level; // 保存级别
        this.minAge = minAge; // 保存最低年龄
        this.requireRole = requireRole; // 保存要求角色
        this.description = description; // 保存课程简介
    }

    public int getId() { // 获取课程编号
        return id; // 返回唯一标识
    }

    public String getTitle() { // 获取课程标题
        return title; // 返回标题内容
    }

    public String getLevel() { // 获取课程级别
        return level; // 返回难度标签
    }

    public int getMinAge() { // 获取建议最低年龄
        return minAge; // 返回年龄数值
    }

    public String getRequireRole() { // 获取所需角色
        return requireRole; // 返回角色名称
    }

    public String getDescription() { // 获取课程简介
        return description; // 返回简介文本
    }

    @Override
    public boolean equals(Object o) { // 重写 equals 判断相等
        if (this == o) { // 如果是同一对象直接返回真
            return true; // 返回 true
        }
        if (o == null || getClass() != o.getClass()) { // 对象为空或类型不同
            return false; // 返回 false
        }
        Course course = (Course) o; // 强制转换为 Course
        return id == course.id; // 以 id 是否相等判断
    }

    @Override
    public int hashCode() { // 重写 hashCode 保持一致
        return Objects.hash(id); // 基于 id 生成哈希值

    }
}
