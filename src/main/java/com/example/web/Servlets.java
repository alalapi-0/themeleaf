package com.example.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

public final class Servlets {
    private static final TemplateEngine TEMPLATE_ENGINE = createTemplateEngine();
    private static volatile JavaxServletWebApplication webApplication;

    private Servlets() {
    }

    public static String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static int paramInt(HttpServletRequest request, String name, int defaultValue) {
        String value = param(request, name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static TemplateEngine templateEngine() {
        return TEMPLATE_ENGINE;
    }

    public static WebContext webContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        JavaxServletWebApplication application = getWebApplication(servletContext);
        IServletWebExchange exchange = application.buildExchange(request, response);
        return new WebContext(exchange, exchange.getLocale());
    }

    private static JavaxServletWebApplication getWebApplication(ServletContext servletContext) {
        JavaxServletWebApplication current = webApplication;
        if (current == null) {
            synchronized (Servlets.class) {
                current = webApplication;
                if (current == null) {
                    current = JavaxServletWebApplication.buildApplication(servletContext);
                    webApplication = current;
                }
            }
        }
        return current;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
