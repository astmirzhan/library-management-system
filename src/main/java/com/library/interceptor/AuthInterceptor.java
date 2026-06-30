package com.library.interceptor;

import com.library.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Interceptor that checks if the user is authenticated.
 * Redirects to login page if no user is in session.
 * Required by EPAM: Spring Interceptors and Authorization checks.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String requestURI = request.getRequestURI();

        if (user == null) {
            logger.warn("Unauthorized access attempt to: {}", requestURI);
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (requestURI.startsWith(request.getContextPath() + "/admin")
                && user.getRole() != User.Role.ADMIN) {
            logger.warn("Forbidden access to admin area by user: {}", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        if (requestURI.startsWith(request.getContextPath() + "/librarian")
                && user.getRole() != User.Role.LIBRARIAN
                && user.getRole() != User.Role.ADMIN) {
            logger.warn("Forbidden access to librarian area by user: {}", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true;
    }
}