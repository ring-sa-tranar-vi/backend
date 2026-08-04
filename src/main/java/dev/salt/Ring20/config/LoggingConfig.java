package dev.salt.Ring20.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class LoggingConfig implements WebMvcConfigurer {

    private static final int MAXIMUM_PERCENTAGE = 100;
    private static final double WARNING_PERCENTAGE = 0.85;
    private static final int MINIMUM_MEMORY = 0;
    private static final long BYTES_PER_MB = 1024L * 1024L;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }

    public static class LoggingInterceptor implements HandlerInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
        private static final Runtime runtime = Runtime.getRuntime();

        @Override
        public boolean preHandle(
                HttpServletRequest request, HttpServletResponse response, Object handler) {
            long usedMemory = getUsedMemory();
            long totalMemory = getTotalMemory();
            long freeMemory = getFreeMemory();

            logger.info(
                    ">>> REQUEST: {} {} | IP: {} | Memory: {}MB / {}MB (Free: {}MB)",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    usedMemory,
                    totalMemory,
                    freeMemory);

            request.setAttribute("startTime", System.currentTimeMillis());
            request.setAttribute("startMemory", usedMemory);
            return true;
        }

        @Override
        public void afterCompletion(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler,
                Exception ex) {
            long startTime = (long) request.getAttribute("startTime");
            long startMemory = (long) request.getAttribute("startMemory");
            long duration = System.currentTimeMillis() - startTime;
            long endMemory = getUsedMemory();
            long memoryDelta = endMemory - startMemory;
            long totalMemory = getTotalMemory();
            long freeMemory = getFreeMemory();

            String memoryChange =
                    memoryDelta >= MINIMUM_MEMORY ? "+" + memoryDelta : String.valueOf(memoryDelta);

            logger.info(
                    "<<< RESPONSE: {} {} | Status: {} | Duration: {}ms | Memory: {}MB / {}MB (Delta: {}MB, Free: {}MB)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    endMemory,
                    totalMemory,
                    memoryChange,
                    freeMemory);

            if (ex != null) {
                logger.error("Exception occurred: ", ex);
            }

            if (endMemory > totalMemory * WARNING_PERCENTAGE) {
                logger.warn(
                        "⚠️  HIGH MEMORY USAGE: {}% of heap used",
                        (int) ((double) endMemory / totalMemory * MAXIMUM_PERCENTAGE));
            }
        }

        private long getUsedMemory() {
            return (runtime.totalMemory() - runtime.freeMemory()) / BYTES_PER_MB;
        }

        private long getTotalMemory() {
            return runtime.totalMemory() / BYTES_PER_MB;
        }

        private long getFreeMemory() {
            return runtime.freeMemory() / BYTES_PER_MB;
        }
    }
}
