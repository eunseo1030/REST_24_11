package com.example.rest_24_10.base.security;

import com.example.rest_24_10.base.security.entryPoint.ApiAuthenticationEntryPoint;
import com.example.rest_24_10.base.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // 아래의 모든 설정은 /api/** 경로에만 적용
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/api/*/member/login").permitAll() // 로그인은 누구나 가능
                                .requestMatchers("/api/*/articles").permitAll() // 글 보기는 누구나 가능
                                .anyRequest().authenticated() // 나머지는 인증된 사용자만 가능
                )
                .cors().disable() // 타 도메인에서 API 호출 가능
                .csrf().disable() // CSRF 토큰 끄기
                .httpBasic().disable() // httpBasic 로그인 방식 끄기
                .formLogin().disable() // 폼 로그인 방식 끄기
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                ) // 세션 끄기
                .addFilterBefore(
                        jwtAuthorizationFilter, // 액세스 토큰으로부터 로그인 처리
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
