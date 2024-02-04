package ckb.platform.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.logging.Logger;

public class SessionInterceptor implements HandlerInterceptor {
    private static final Logger log = Logger.getLogger(SessionInterceptor.class.getName());
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        log.info("SessionInterceptor:" + request.getRequestURI());
        if (!(request.getRequestURI().contains("login") || request.getRequestURI().contains("index.html")) && request.getRequestURI().contains("html") && session.getAttribute("user") == null) {
            log.info("SessionInterceptor: ----redirect to login----");
            response.sendRedirect("login");
            return false;
        }
        return true;
    }
}
