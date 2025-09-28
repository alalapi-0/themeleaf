package com.example.web; // 页面路由 Servlet 包

import org.thymeleaf.TemplateEngine; // 模板引擎
import org.thymeleaf.context.WebContext; // 模板上下文

import javax.servlet.ServletException; // Servlet 异常
import javax.servlet.annotation.WebServlet; // Servlet 映射注解
import javax.servlet.http.HttpServlet; // 基础 HttpServlet
import javax.servlet.http.HttpServletRequest; // 请求对象
import javax.servlet.http.HttpServletResponse; // 响应对象
import java.io.IOException; // IO 异常
import java.nio.charset.StandardCharsets; // UTF-8 常量

@WebServlet(urlPatterns = {"/", "/tags"}) // 映射首页与标签页
public class PageServlet extends HttpServlet { // 简单页面渲染 Servlet
    private final transient TemplateEngine templateEngine = Servlets.templateEngine(); // 复用模板引擎

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { // 处理 GET 请求
        String path = req.getServletPath(); // 获取请求路径
        String view = "/tags".equals(path) ? "tags" : "index"; // 根据路径选择模板名

        WebContext context = Servlets.webContext(req, resp, getServletContext()); // 构建模板上下文

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 设置编码
        resp.setContentType("text/html;charset=UTF-8"); // 设置响应类型
        templateEngine.process(view, context, resp.getWriter()); // 渲染并输出页面
    }
}
