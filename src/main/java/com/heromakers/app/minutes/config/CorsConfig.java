package com.heromakers.app.minutes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class CorsConfig {
    //@Value("${cors.allow.url}")
    private final String allowUrl = "*"; // http://localhost:8087";

    @Bean
    public FilterRegistrationBean<Filter> corsFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter(allowUrl));
        registration.setName("CorsFilter");
        registration.setOrder(-200); // before Spring Security
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        return registration;
    }

    @RequiredArgsConstructor
    private static class CorsFilter implements Filter {
        private final String allowUrl;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (!"none".equalsIgnoreCase(allowUrl)) {

            }
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, X-AUTH-TOKEN");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

            filterChain.doFilter(request, response);
        }
    }
}