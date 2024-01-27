package com.heromakers.app.minutes.security;

import com.heromakers.app.minutes.common.ResultStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ResultStatus exception = (ResultStatus) request.getAttribute("exception");
        log.debug("log: exception: {} ", exception);
        if(exception != null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().println("{ \"message\" : \"" + "Token is "  + exception
                    + "\", \"status\" : " + "\"" + exception + "\" } ");
        }
    }

}
