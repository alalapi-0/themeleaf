package com.example.web; // Web 层工具类所在包

import org.thymeleaf.TemplateEngine; // Thymeleaf 模板引擎
import org.thymeleaf.context.WebContext; // Thymeleaf Web 上下文
import org.thymeleaf.templatemode.TemplateMode; // 模板模式配置
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver; // 类加载器资源解析器
import org.thymeleaf.web.servlet.IServletWebExchange; // Servlet 交换对象
import org.thymeleaf.web.servlet.JavaxServletWebApplication; // Thymeleaf Servlet 集成入口

import javax.servlet.ServletContext; // Servlet 上下文
import javax.servlet.http.HttpServletRequest; // 请求对象
import javax.servlet.http.HttpServletResponse; // 响应对象
import java.nio.charset.StandardCharsets; // 字符集常量

public final class Servlets { // 提供静态辅助方法
    private static final TemplateEngine TEMPLATE_ENGINE = createTemplateEngine(); // 预先构建的模板引擎实例
    private static volatile JavaxServletWebApplication webApplication; // 缓存 Thymeleaf Web 应用

    private Servlets() { // 禁止实例化
    }

    public static String param(HttpServletRequest request, String name) { // 读取并清理字符串参数
        String value = request.getParameter(name); // 从请求获取原始值
        if (value == null) { // 没有参数时返回 null
            return null; // 空值直接返回
        }
        String trimmed = value.trim(); // 去掉前后空白
        return trimmed.isEmpty() ? null : trimmed; // 空字符串统一视为 null
    }

    public static int paramInt(HttpServletRequest request, String name, int defaultValue) { // 读取整数参数
        String value = param(request, name); // 复用字符串读取逻辑
        if (value == null) { // 如果没有值
            return defaultValue; // 返回默认值
        }
        try {
            return Integer.parseInt(value); // 尝试解析整数
        } catch (NumberFormatException ex) { // 捕获格式错误
            return defaultValue; // 解析失败仍返回默认值
        }
    }

    public static TemplateEngine templateEngine() { // 暴露模板引擎
        return TEMPLATE_ENGINE; // 返回单例引擎
    }

    public static WebContext webContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) { // 构建 Thymeleaf 上下文
        JavaxServletWebApplication application = getWebApplication(servletContext); // 获取 Web 应用桥接对象
        IServletWebExchange exchange = application.buildExchange(request, response); // 创建 Servlet 交换对象
        return new WebContext(exchange, exchange.getLocale()); // 使用交换对象和本地化信息创建上下文
    }

    private static JavaxServletWebApplication getWebApplication(ServletContext servletContext) { // 懒加载 Web 应用
        JavaxServletWebApplication current = webApplication; // 读取缓存引用
        if (current == null) { // 首次访问时
            synchronized (Servlets.class) { // 双重检查锁保证线程安全
                current = webApplication; // 再次读取
                if (current == null) { // 仍未初始化
                    current = JavaxServletWebApplication.buildApplication(servletContext); // 创建新实例
                    webApplication = current; // 缓存起来
                }
            }
        }
        return current; // 返回可用实例
    }

    private static TemplateEngine createTemplateEngine() { // 构建 Thymeleaf 模板引擎
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(); // 使用类路径解析模板
        resolver.setTemplateMode(TemplateMode.HTML); // 模板模式设为 HTML
        resolver.setPrefix("/templates/"); // 指定模板前缀路径
        resolver.setSuffix(".html"); // 模板后缀
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 设置字符集
        resolver.setCacheable(false); // 开发阶段关闭缓存

        TemplateEngine engine = new TemplateEngine(); // 创建引擎
        engine.setTemplateResolver(resolver); // 注入解析器
        return engine; // 返回配置完成的引擎
    }
}
