package com.example.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(urlPatterns = {"/", "/tags"})
public class PageServlet extends HttpServlet {
    private final transient TemplateEngine templateEngine = Servlets.templateEngine();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String view = "/tags".equals(path) ? "tags" : "index";

        WebContext context = Servlets.webContext(req, resp, getServletContext());

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process(view, context, resp.getWriter());
    }
}
