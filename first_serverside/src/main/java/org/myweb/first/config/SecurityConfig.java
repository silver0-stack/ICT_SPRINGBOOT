package org.myweb.first.config;

import jakarta.servlet.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration      //스프링 부트의 설정을 담당하는 클래스임을 나타내는 어노테이션임
@EnableWebSecurity  //스프링 시큐리티 설정을 활성화함
//@RequiredArgsConstructor  //매개변수 있는 생성자를 자동 생성해서, 멤버변수에 대한 의존성 주입 자동 처리함
public class SecurityConfig {
    //SpringSecurity 는 문지기의 역할을 하는 이 애플리케이션을 지키는 보안 시스템과 같음
    //문을 열려면 비밀번호를 입력해야 하듯이, 인터넷 상에서 웹사이트에 들어오는 요청들을 확인(인증 및 인가)해서
    //누구는 들어오게 하고 누구는 못 들어오게 하는 역할을 수행함

    //1. 멤버변수 추가 -----------------------------------------
    private final AuthenticationConfiguration authenticationConfiguration;
    //이 클래스의 가장 중요한 역할은 AuthenticationManager 를 생성 및 제공하는 것임
    //인증 제공자(데이터베이스 인증, OAuth2 인증, 커스텀 인증: jwt 등) 간의 충돌을 방지하고,
    // 적절히 초기화하며, 인증 과정을 매끄럽게 처리할 수 있도록 하는 로직을 담당함
    // Spring Boot 와 함께 사용할 때, AuthenticationConfiguration 은 기본 인증 전략을 자동으로 연계하는 역할을 함

    //2. 생성자를 통한 의존성 주입(멤버변수 객체 생성)으로 필요한 서비스와 설정을 초기화함
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    //3. 인증(Authentication, 보안검사) 관리자를 스프링 컨테이너에 Bean 으로 등록함, 인증 과정에서 중요한 클래스임
    // AuthenticationManager : 사용자의 인증 요청을 처리하는 핵심 클래스임
    // 사용법 : 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //HTTP 관련 보안 설정을 정의함
    //SecurityFilterChain 을 Bean 으로 등록하고, http 요청에 대한 보안을 구성함
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //보안 시스템 설정하기를 담당하는 메소드임
        //웹 사이트의 규칙을 정해줘야 함

        http
                //disable 처리 설정 ----------------------------------------------------------------
                //csrf(Cross-Site-Request-Forgery)
                //예 : <img src="https://kakao.developer.com/login/kakao.png'> 사용 못 하게 함
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (필요 시 활성화)
                .formLogin(AbstractHttpConfigurer::disable) // SpringSecurity 가 기본 제공하는 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // 시큐리티가 제공하는 로그인을 사용 못하게 함
                //어떤 요청은 통과시키고, 막을 것인지를 정하는 부분임 -----------------
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/notice/**", "/board/**").permitAll() // 특정 경로는 인증 없이 접근 허용
                        //그 외의 모든 요청은 보안인증 검사를 진행해서 통과 여부를 결정함
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                )
            //세션 정책을 STATELESS 로 설정하고, 세션을 사용하지 않는 것을 명시함
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    //시큐리티 작동시 SecurityChainFilter 들이 순서대로 자동 작동되는지 확인
    //디버그용 필터
    static class DebugFilter implements Filter {
        private final String filterName;

        public DebugFilter(String filterName) {
            this.filterName = filterName;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.println("[DEBUG] Entering Filter : " + filterName);
            //현재 SecurityContext 상태 출력
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null) {
                System.out.println("[DEBUG] Authentication : " + authentication.getName()
                        + ", Authorization : " + authentication.getAuthorities());
            }else{
                System.out.println("[DEBUG] Authentication is null");
            }

            chain.doFilter(request, response);
            System.out.println("[DEBUG] Exiting Filter : " + filterName);
        }

    }

}
