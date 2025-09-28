package com.example.web;

import com.example.model.Course;
import com.example.repo.InMemoryCourseRepo;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@WebServlet(urlPatterns = "/courses/detail")
public class CourseDetailServlet extends HttpServlet {
    private final transient InMemoryCourseRepo repo = InMemoryCourseRepo.getInstance();
    private final transient TemplateEngine templateEngine = Servlets.templateEngine();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Servlets.paramInt(req, "id", -1);
        Optional<Course> courseOpt = id > 0 ? repo.findById(id) : Optional.empty();

        WebContext context = Servlets.webContext(req, resp, getServletContext());
        context.setVariable("course", courseOpt.orElse(null));
        context.setVariable("id", id);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("courses/detail", context, resp.getWriter());
    }
}
