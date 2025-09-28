package com.example.web; // Servlet 所在包

import com.example.model.Course; // 引入课程实体
import com.example.repo.InMemoryCourseRepo; // 引入仓库
import org.thymeleaf.TemplateEngine; // 模板引擎
import org.thymeleaf.context.WebContext; // 模板上下文

import javax.servlet.ServletException; // Servlet 异常
import javax.servlet.annotation.WebServlet; // 路由注解
import javax.servlet.http.HttpServlet; // Servlet 基类
import javax.servlet.http.HttpServletRequest; // 请求对象
import javax.servlet.http.HttpServletResponse; // 响应对象
import java.io.IOException; // IO 异常
import java.nio.charset.StandardCharsets; // UTF-8 常量
import java.util.Optional; // Optional 包装

@WebServlet(urlPatterns = "/courses/detail") // 绑定详情页路由
public class CourseDetailServlet extends HttpServlet { // 课程详情 Servlet
    private final transient InMemoryCourseRepo repo = InMemoryCourseRepo.getInstance(); // 单例仓库
    private final transient TemplateEngine templateEngine = Servlets.templateEngine(); // 模板引擎实例

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { // 处理 GET 请求
        int id = Servlets.paramInt(req, "id", -1); // 读取课程编号
        Optional<Course> courseOpt = id > 0 ? repo.findById(id) : Optional.empty(); // 根据编号查询课程

        WebContext context = Servlets.webContext(req, resp, getServletContext()); // 创建模板上下文
        context.setVariable("course", courseOpt.orElse(null)); // 将课程对象放入上下文
        context.setVariable("id", id); // 传入查询编号用于提示

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 设置响应编码
        resp.setContentType("text/html;charset=UTF-8"); // 设置内容类型
        templateEngine.process("courses/detail", context, resp.getWriter()); // 渲染课程详情模板
    }
}
