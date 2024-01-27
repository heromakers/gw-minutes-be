package com.heromakers.app.minutes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

//    @Qualifier
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring().requestMatchers("/libs/**", "/plugins/**",
//                "/bootstrap/**", "/dist/**", "/font-awesome/**", "/material_icons/**", "/pe-icon-7-stroke/**", "/themify-icons/**", "/weather-icons/**",
//                "/*/images/**", "/*/img/**", "/*/js/**", "/*/css/**", "/*/fonts/**", "/*/**.css", "/*/**.js");
//        return webSecurity.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        String permitAllUrls[] = {
                "/libs/**", "/plugins/**",
                "/bootstrap/**", "/dist/**", "/font-awesome/**", "/material_icons/**", "/pe-icon-7-stroke/**", "/themify-icons/**", "/weather-icons/**",
                "/*/images/**", "/*/img/**", "/*/js/**", "/*/css/**", "/*/fonts/**", "/*/**.css", "/*/**.js",
                "/", "/swagger-ui/*", "/admin/**",
                "/api/login", "/api/join", "/api/google", "/api/kakao",
                "/api/refresh-token", "/api/find-account", "/api/reset-password", "/api/temp-password",
        };

        httpSecurity.csrf( AbstractHttpConfigurer::disable ) // csrf -> csrf.disable() )
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(config ->
                        config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers(permitAllUrls).permitAll()
                                .requestMatchers("/*/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}