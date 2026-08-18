package org.acme.inventory.listener;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationRunner {

    private static final String DIVIDER = "=".repeat(79);

    private final Environment environment;
    private final SwaggerUiConfigProperties swaggerUiConfig;

    @Override
    public void run(ApplicationArguments args) {
        String appName = environment.getProperty("spring.application.name", "application");
        String baseUrl = resolveUrl("");
        String swaggerUrl = resolveUrl(swaggerUiConfig.getPath());

        log.info("""

                {}
                  {}
                  Application started on {}
                  UI available at {}
                  Swagger UI available at {}
                {}
                """, DIVIDER, appName, baseUrl, baseUrl, swaggerUrl, DIVIDER);
    }

    private String resolveUrl(String path) {
        String port = environment.getProperty("local.server.port",
                environment.getProperty("server.port", "8080"));
        String contextPath = normalizeContextPath(
                environment.getProperty("server.servlet.context-path", ""));
        return "http://localhost:" + port + contextPath + normalizePath(path);
    }

    private static String normalizeContextPath(String contextPath) {
        if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath)) {
            return "";
        }
        return contextPath.endsWith("/")
                ? contextPath.substring(0, contextPath.length() - 1)
                : contextPath;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
