package com.example.web; // Web 层 Servlet 包

import com.example.model.Course; // 引入课程实体
import com.example.repo.InMemoryCourseRepo; // 引入内存仓库
import org.thymeleaf.TemplateEngine; // 模板引擎
import org.thymeleaf.context.WebContext; // Web 上下文

import javax.servlet.ServletException; // Servlet 异常
import javax.servlet.annotation.WebServlet; // 路由注解
import javax.servlet.http.HttpServlet; // HttpServlet 基类
import javax.servlet.http.HttpServletRequest; // 请求对象
import javax.servlet.http.HttpServletResponse; // 响应对象
import java.io.IOException; // IO 异常
import java.nio.charset.StandardCharsets; // UTF-8 常量
import java.util.Comparator; // 排序工具
import java.util.List; // 列表容器
import java.util.Locale; // 本地化工具
import java.util.stream.Collectors; // 流收集器

@WebServlet(urlPatterns = "/courses") // 映射课程列表路由
public class CourseListServlet extends HttpServlet { // 课程列表 Servlet
    private final transient InMemoryCourseRepo repo = InMemoryCourseRepo.getInstance(); // 注入单例仓库
    private final transient TemplateEngine templateEngine = Servlets.templateEngine(); // 获取共享模板引擎

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { // 处理 GET 请求
        String keyword = Servlets.param(req, "keyword"); // 读取搜索关键字
        int page = Servlets.paramInt(req, "page", 1); // 当前页码
        int size = Servlets.paramInt(req, "size", 5); // 每页条数
        if (size < 1) { // 防止非法分页大小
            size = 5; // 兜底默认值
        }

        List<Course> filtered = repo.findAll().stream() // 获取所有课程并转换为流
                .sorted(Comparator.comparing(Course::getId)) // 按编号排序
                .filter(course -> matchesKeyword(course, keyword)) // 按关键字过滤
                .collect(Collectors.toList()); // 收集为列表

        int total = filtered.size(); // 总条数
        if (page < 1) { // 页码小于 1 时
            page = 1; // 重置为第一页
        }
        int start = (page - 1) * size; // 计算起始索引
        if (start >= total && total > 0) { // 如果超出范围
            page = 1; // 重置页码
            start = 0; // 起点回到 0
        }
        int end = Math.min(start + size, total); // 计算结束索引
        List<Course> pageContent = filtered.subList(start, end); // 截取当前页内容

        WebContext context = Servlets.webContext(req, resp, getServletContext()); // 创建模板上下文
        context.setVariable("courses", pageContent); // 绑定课程列表
        context.setVariable("total", total); // 绑定总数
        context.setVariable("page", page); // 绑定页码
        context.setVariable("size", size); // 绑定分页大小
        context.setVariable("keyword", keyword == null ? "" : keyword); // 绑定搜索关键字

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 设置响应编码
        resp.setContentType("text/html;charset=UTF-8"); // 设置 Content-Type
        templateEngine.process("courses/list", context, resp.getWriter()); // 渲染模板输出
    }

    private boolean matchesKeyword(Course course, String keyword) { // 判断课程是否匹配关键字
        if (keyword == null || keyword.isEmpty()) { // 关键字为空则全部匹配
            return true; // 返回 true
        }
        String lower = keyword.toLowerCase(Locale.ENGLISH); // 将关键字转为小写
        return course.getTitle().toLowerCase(Locale.ENGLISH).contains(lower) // 标题包含关键字
                || course.getDescription().toLowerCase(Locale.ENGLISH).contains(lower); // 或简介包含关键字
    }
}
