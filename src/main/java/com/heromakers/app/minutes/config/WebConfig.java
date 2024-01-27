package com.heromakers.app.minutes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }

    // ToDo :: 404 500 //

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
                .allowedMethods("*")
                .allowCredentials(false)
                .allowedHeaders("*")
                .allowedOrigins("*");
//                .allowedOrigins("http://localhost:8081");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoggerInterceptor())
//                .addPathPatterns("/**");

        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/admin/**")
                .addPathPatterns("/")
                .excludePathPatterns("/admin/login", "/admin/join", "/admin/find-account", "/admin/temp-password");
    }

}
