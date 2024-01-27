package com.heromakers.app.minutes.security;

import com.heromakers.app.minutes.common.ResultStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void authToken() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        String token = jwtTokenProvider.generateAuthToken("admin", roles);
        System.out.println("token = " + token);
        assertEquals(ResultStatus.success, jwtTokenProvider.validateToken(token));
        assertEquals("admin", jwtTokenProvider.getUsername(token));
    }

    @Test
    void refreshToken() {
        String token = jwtTokenProvider.generateRefreshToken();
        assertEquals(ResultStatus.success, jwtTokenProvider.validateToken(token));
    }
}