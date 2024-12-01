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
@EnableWebSecurity // 웹 보안 활성화: SpringSecurity의 기본 보안 설정을 활성화하고 추가적인 보안 구성을 할 수 있게함
@EnableMethodSecurity // 메소드 수준의 보안을 활성화: 특정 메소드에 @PreAuthorize 같은 어노테이션 사용할 수 있게함
public class SecurityConfig {
    @Autowired
    private JwtUtil jwtUtil; // JWT 유틸리티 클래스(JWT 토큰을 생성하고 검증하는 유틸리티 클래스) 주입: JWT 기반 인증을 구현하는 데 사용된다.

    /**
     * JWT 인증 필터 핀 등록: 이 필터는 JWT 토큰을 검사하고 유효한 경우 인증 정보를 스프링 시큐리티 컨텍스트에 설정한다
     * @return JwtAuthenticationFilter(커스텀 JWT 인증 필터) 인스턴스: JwtUtil을 사용하여 토큰을 검증하고 인증 정보를 설정한다.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * HTTP 보안 필터 체인 설정 메소드
     * @param http HttpSecurity 객체를 사용하여 보안 필터 체인을 설정한다.
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
                            corsConfig.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5000")); // 실제 사용 도메인으로 제한
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            corsConfig.setAllowedHeaders(List.of("*")); // 모든 헤더를 허용한다
                            corsConfig.setAllowCredentials(false); // 클라이언트가 인증 정보를 포함한 요청을 보낼 수 없도록 설정한다.
                            return corsConfig;
                        }))
                // 권한 룰 설정: HTTP 요청에 대한 권한 규칙을 설정한다.
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인, 회원가입, ID 체크 엔드포인트에 대한 모든 요청 허용: 인증되지 않은 사용자도 접근할 수 있어야 함
                        .requestMatchers("/api/members/login", "/api/members/enroll", "/api/members/idchk", "/api/members/photo/**").permitAll()
                        .requestMatchers("/api/profile-pictures/**").permitAll()

                        // /api/chat/save 엔드포인트에 대한 POST 요청 허용
                        .requestMatchers(HttpMethod.POST, "/api/chat/save").hasAnyRole("SENIOR", "MANAGER", "FAMILY", "ADMIN")

                        // GET /api/members/** 엔드포인트는 SENIOR, MANAGER, FAMILY, ADMIN에게 허용
                        .requestMatchers(HttpMethod.GET, "/api/members/**", "/api/chat/**").hasAnyRole("SENIOR", "MANAGER", "FAMILY", "ADMIN")
                        // 그 외의 /api/members/** 엔드포인트(POST, PUT, DELETE 등)는 SENIOR, MANAGER, FAMILY, ADMIN, AI에게 허용
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasAnyRole("SENIOR", "MANAGER", "FAMILY", "ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 접근 허용
                        // 그 외 모든 명시하지 않은 요청은 인증된 사용자이기만 하면 접근 가능: 보안강화를 위해 기본적으로 모든 요청에 대해 인증 요구
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가: Spring Security의 기본 인증 필터 전에 JwtAuthenticationFilter를 실행한다.: 인증 과정에서 JWT 토큰을 먼저 처리하게 됨
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 보안 필터 체인 빌드 후 반환
        return http.build();
    }
}
