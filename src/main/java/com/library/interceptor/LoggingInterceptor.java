package com.library.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor that logs every HTTP request and response.
 * Required by EPAM: Spring Interceptors and Logging.
 */
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(LoggingInterceptor.class);
    private static final String START_TIME_ATTR = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        logger.info("[{}] {} {}", request.getMethod(), request.getRequestURI(),
                request.getQueryString() != null ? "?" + request.getQueryString() : "");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long start = (Long) request.getAttribute(START_TIME_ATTR);
        long duration = System.currentTimeMillis() - start;
        logger.info("[{}] {} -> {} ({}ms)", request.getMethod(), request.getRequestURI(),
                response.getStatus(), duration);

        if (ex != null) {
            logger.error("Request failed with exception", ex);
        }
    }
}