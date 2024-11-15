package org.myweb.first.config;

import org.myweb.first.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

/**
 * Spring Security 설정 클래스
 * JWT 기반 인증을 구현하기 위해 보안 필터 체인 구성
 */
@Configuration // 스프링 설정 클래스
@EnableWebSecurity // 웹 보안 활성화
@EnableMethodSecurity // 메소드 보안 활성화
public class SecurityConfig {
    @Autowired
    private JwtUtil jwtUtil; // JWT 유틸리티 클래스 주입

    /**
     * JWT 인증 필터 핀 등록
     * @return JwtAuthenticationFilter 인스턴스
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * 보안 필터 체인 설정 메소드
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // CSRF 비활성화 (REST API에서는 일반적으로 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리: STATELESS 설정 (JWT 기반 인증이므로 서버에서 세션 관리 안 함)
                // 모든 요청은 독립적으로 처리한다
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS 설정
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                            corsConfig.setAllowedOrigins(List.of("*")); // 모든 도메인 허용하지만 나중엔 특정 도메인 허용(리액트 주소) 등으로 보안 처리해야 함
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
                        // 그 외 모든 요청은 인증된 사용자이기만 하면 접근 가능
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가: Spring Security의 기본 인증 필터 전에 JwtAuthenticationFilter를 실행한다.
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 보안 필터 체인 빌드 후 반환
        return http.build();
    }
}
