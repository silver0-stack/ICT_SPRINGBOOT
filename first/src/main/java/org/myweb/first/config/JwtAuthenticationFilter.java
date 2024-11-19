package org.myweb.first.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myweb.first.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * JWT 인증 필터 클래스
 * 클라이언트 요청의 헤더에서 JWT를 추출하여 검증하고, 유효할 경우 인증 정보를 SecurityContextHolder에 저장
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // 로깅을 위한 Logger

    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스

    /**
     * 생성자 주입
     * @param jwtUtil JWT 유틸리티 클래스 인스턴스
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 특정 경로는 필터링에서 제외
     * @param request HttpServletRequest 객체
     * @return 필터링하지 않을지 여부
     * @throws ServletException 예외 발생 시
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath(); // 요청 경로 추출
        boolean shouldNotFilter = path.equals("/api/members/login") ||
                path.equals("/api/members/enroll") ||
                path.equals("/api/members/idchk") ||
                path.startsWith("/swagger-ui/") || // Swagger UI의 모든 정적 리소스(CSS, JS, 이미지) 제외
                path.startsWith("/v3/api-docs/"); // OpenAPI 문서 제외;
        // 현재 요청이 필터링에서 제외되고 있는지 확인할 수 있음
        logger.debug("Request URI: {}, shouldNotFilter: {}", path, shouldNotFilter); // 필터링 여부 로그
        return shouldNotFilter; // 제외 여부 반환
    }


    /**
     * 요청마다 JWT 유효성 검사 및 인증 처리
     * @param request HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param filterChain 필터체인
     * @throws ServletException 예외 발생 시
     * @throws IOException 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION); // Authorization 헤더에서 토큰 추출
            logger.debug("Authorization Header: {}", bearerToken); // 토큰 로그

            String jwt = getJwtFromRequest(request); // 토큰 추출
            logger.debug("Extracted JWT Token: {}", jwt); // 추출된 토큰 로그

            /* 서명 및 만료 시간 확인해서 유효한 토큰인지 검증 */
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                // JWT에서 사용자 ID 및 권한 추출
                String userId = jwtUtil.extractUserId(jwt);
                String roles = jwtUtil.extractRoles(jwt);

                logger.debug("Extracted UserId: {}", userId); // 사용자 ID 로그
                logger.debug("Extracted Roles: {}", roles); // 역할 정보 로그

                if (StringUtils.hasText(userId) && roles != null && !roles.isEmpty()) {
                    // JWT claims에 ROLE_ 접두사가 있어야만 SecurityContextHolder에 인증 정보를 저장할 수 있음
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roles));// ROLE_ 접두사 추가

                    // 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);

                    // 인증 상세 정보 설정
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContextHolder에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User authenticated: {}", userId); // 인증 성공 로그
                } else {
                    // 클레임에 필요한 정보가 없을 경우
                    logger.warn("JWT Token does not contain required claims");
                    // 401 응답 반환
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token does not contain required claims");
                    return;
                }
            } else {
                // 토큰이 없거나 유효하지 않을 경우
                logger.debug("JWT Token is missing or invalid");
                // 401 응답 반환
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is missing or invalid");
                return;
            }
        } catch (ExpiredJwtException ex) {
            // 토큰 만료 시
            logger.error("JWT Token has expired", ex);
            // 401 응답 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
            return;
        } catch (UnsupportedJwtException ex) {
            // 지원하지 않는 토큰 형식 시
            logger.error("Unsupported JWT Token", ex);
            // 401 응답 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT Token");
            return;
        } catch (MalformedJwtException ex) {
            // 잘못된 토큰 형식 시
            logger.error("Malformed JWT Token", ex);
            // 401 응답 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT Token");
            return;
        } catch (IllegalArgumentException ex) {
            // 토큰이 null이거나 비어있을 경우
            logger.error("JWT Token compact of handler are invalid", ex);
            // 401 응답 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
            return;
        } catch (Exception e) {
            // 기타 예외 처리
            logger.error("Could not set user authentication in security context", e);
            // 500 응답 반환
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
            return;
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }


    /**
     * Authorization 헤더에서 JWT 추출 메소드
     * @param request HttpServletRequest 객체
     * @return 추출된 JWT 토큰 문자열 또는 null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION); // Authorization 헤더에서 토큰 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim(); // "Bearer " 접두사 제거 후 반환
        }
        return null; // 토큰이 없거나 잘못된 형식일 경우 null 반환
    }
}
