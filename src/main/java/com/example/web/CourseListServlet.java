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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/courses")
public class CourseListServlet extends HttpServlet {
    private final transient InMemoryCourseRepo repo = InMemoryCourseRepo.getInstance();
    private final transient TemplateEngine templateEngine = Servlets.templateEngine();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = Servlets.param(req, "keyword");
        int page = Servlets.paramInt(req, "page", 1);
        int size = Servlets.paramInt(req, "size", 5);
        if (size < 1) {
            size = 5;
        }

        List<Course> filtered = repo.findAll().stream()
                .sorted(Comparator.comparing(Course::getId))
                .filter(course -> matchesKeyword(course, keyword))
                .collect(Collectors.toList());

        int total = filtered.size();
        if (page < 1) {
            page = 1;
        }
        int start = (page - 1) * size;
        if (start >= total && total > 0) {
            page = 1;
            start = 0;
        }
        int end = Math.min(start + size, total);
        List<Course> pageContent = filtered.subList(start, end);

        WebContext context = Servlets.webContext(req, resp, getServletContext());
        context.setVariable("courses", pageContent);
        context.setVariable("total", total);
        context.setVariable("page", page);
        context.setVariable("size", size);
        context.setVariable("keyword", keyword == null ? "" : keyword);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("courses/list", context, resp.getWriter());
    }

    private boolean matchesKeyword(Course course, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase(Locale.ENGLISH);
        return course.getTitle().toLowerCase(Locale.ENGLISH).contains(lower)
                || course.getDescription().toLowerCase(Locale.ENGLISH).contains(lower);
    }
}
