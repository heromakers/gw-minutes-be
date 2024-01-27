package com.heromakers.app.minutes.security;

import com.heromakers.app.minutes.common.ResultStatus;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret.key}")
    private String secretKey;

    private String signKey;

    private static final long AUTH_TOKEN_VALID_MS = 1000L * 60 * 60 * 9; // 9시간만 Token 유효

    private static final long REFRESH_TOKEN_VALID_MS = 1000L * 60 * 60 * 24 * 7; // 7일 Token 유효

    @Autowired
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        signKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Auth Token 생성
    public String generateAuthToken(String username, List<String> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // Token 발행일자
                .setExpiration(new Date(now.getTime() + AUTH_TOKEN_VALID_MS)) // Expire Time
                .signWith(SignatureAlgorithm.HS256, signKey) // 암호화 알고리즘, secret
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_MS))
                .signWith(SignatureAlgorithm.HS256, signKey)
                .compact();
    }

    // Auth Token => 인증 정보 조회
    public Authentication getAuthentication(String authToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(authToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Auth Token => username(회원 식별 정보) 추출
    public String getUsername(String authToken) {
        return Jwts.parser().setSigningKey(signKey).parseClaimsJws(authToken).getBody().getSubject();
    }

    // Request - Header : "X-AUTH-TOKEN" => authToken
    public String resolveAuthToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // Request - Header : "X-REFRESH-TOKEN" => refreshToken
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("X-REFRESH-TOKEN");
    }

    // authToken 의 유효성 + 만료일자 확인
    public ResultStatus validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(signKey).parseClaimsJws(token);
            if(claims.getBody().getExpiration().before(new Date())){
                return ResultStatus.expired;
            } else {
                return ResultStatus.success;
            }
        } catch (MalformedJwtException e) {
            return ResultStatus.fail;
        } catch (ExpiredJwtException e) {
            return ResultStatus.expired;
        } catch (AccessDeniedException e){
            return ResultStatus.denied;
        } catch (JwtException e) {
            return ResultStatus.invalid;
        }
    }
}
