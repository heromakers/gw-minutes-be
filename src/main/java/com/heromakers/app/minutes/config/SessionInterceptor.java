package com.heromakers.app.minutes.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String roles = (String) session.getAttribute("roles");

        if(roles == null) {
            response.sendRedirect("/admin/login");
        } else if(!roles.contains("ADMIN") && !roles.contains("MANAGER")) {
            System.out.println("roles: " + roles);
            session.invalidate();
            response.sendRedirect("/admin/login");
        }

        return true;
    }
}