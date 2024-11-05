package org.myweb.first.config;

import org.myweb.first.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // 우리가 JWT를 사용하기 때문에 CSRF는 사용 불가
                .csrf(csrf -> csrf.disable()) // REST API에서는 CSRF 보호를 비활성화. 왜?
                // 세션 관리를 STATELESS 로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 필요하다면 CORS를 설정한다
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                            corsConfig.setAllowedOrigins(List.of("*")); // 필요한 대로 적용하기
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 필요한 HTTP Method 적용
                            corsConfig.setAllowedMethods(List.of("*"));
                            corsConfig.setAllowCredentials(false);
                            return corsConfig;
                        }))

                // 권한 룰을 설정한다
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인, 회원가입, ID 체크 엔드포인트 등 전체 허용
                        .requestMatchers("/api/**").permitAll() // "/api/**" 경로는 허용
                        .anyRequest().authenticated() //기타 모든 요청은 인증 필요
                )

               // UsernamePasswordAuthenticationFilter 전에 JWT 필터를 추가하기
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWTFilter를 SecurityFilterChain에 추가

        return http.build(); // SecurityFilterChain 반환
    }
}
