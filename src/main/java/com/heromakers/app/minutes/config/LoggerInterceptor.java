package com.heromakers.app.minutes.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

@Component
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

//    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if( requestURI.contains("/plugins/") ||
            requestURI.contains("/libs/") ||
            requestURI.contains("/images/") ||
            requestURI.contains("/img/") ||
            requestURI.contains("/fonts/") ||
            requestURI.contains("/js/") ||
            requestURI.contains(".js") ||
            requestURI.contains("/css/") ||
            requestURI.contains(".css") ||
            requestURI.contains("/swagger-ui/")) {
            return true;
        }
        if(!log.isInfoEnabled()){
            return true;
        }
        log.info("Request URI : " + requestURI );
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String name = (String) params.nextElement();
            log.info("Parameter : " + name + " : " + request.getParameter(name));
        }

        return true;
    }
}