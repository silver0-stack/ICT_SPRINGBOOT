package org.myweb.first.config;

import org.myweb.first.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

/* JWT를 사용한 인증을 구현하기 위해 Spring Security를 설정한다 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // CSRF 비활성화 (REST API에서는 일반적으로 필요 없음)
                .csrf(csrf -> csrf.disable())
                // 세션 관리: STATELESS 설정 (JWT 기반 인증이므로 서버에서 세션 관리 안 함)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS 설정
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                            corsConfig.setAllowedOrigins(List.of("*")); // 보안상 특정 도메인으로 제한 권장
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            corsConfig.setAllowedHeaders(List.of("*"));
                            corsConfig.setAllowCredentials(false);
                            return corsConfig;
                        }))
                // 권한 룰 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인, 회원가입, ID 체크 엔드포인트은 모두 허용
                        .requestMatchers("/api/members/login", "/api/members/enroll", "/api/members/idchk", "/api/members/{userId}").permitAll()
                        // GET /api/members/** 엔드포인트는 ROLE_USER 또는 ROLE_ADMIN에게 허용
                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasAnyRole("USER", "ADMIN")
                        // 그 외의 /api/members/** 엔드포인트는 ROLE_ADMIN에게만 허용
                        .requestMatchers("/api/members/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
