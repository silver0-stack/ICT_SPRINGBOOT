package org.myweb.first.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 생성 및 검증을 위한 유틸리티 클래스
 */
@Component // 스프링 컴포넌트로 등록
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class); // 로깅을 위한 Logger

    @Value("${jwt.secret}") // application.properties에서 jwt.secret 값 주입
    private String SECRET_KEY;

    @Value("${jwt.expiration}") // application.properties에서 jwt.expiration 값 주입
    private long EXPIRATION_TIME; // 토큰 만료 시간(밀리초 단위)

    private Key key; // JWT 서명에 사용할 비밀키

    /**
     * Bean 초기화 시 비밀키 디코딩 및 Key 객체 생성
     */
    @PostConstruct
    public void init() {
        try {
            byte[] decodedSecret = Base64.getDecoder().decode(SECRET_KEY); //Base64로 인코딩된 비밀키 디코딩
            this.key = Keys.hmacShaKeyFor(decodedSecret); // HMAX SHA 키 생성
            logger.info("JWT Secret Key initialized successfully."); // 초기화 성공 로그
            logger.info("JWT Expiration Time: {} ms", EXPIRATION_TIME); // 만료 시간 로그
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64-encoded secret key", e); //비밀키 디코딩 오류 로그
            throw e; // 예외 재발생
        }
    }

    /**
     * JWT 토큰 생성 메소드
     * @param userId 사용자 ID
     * @param roles 사용자 역할
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId, String roles) {
        return Jwts.builder()
                .setSubject(userId) // 토큰의 주제 설정 (사용자 ID)
                .claim("roles", roles) // 사용자 역할 클레임 추가
                .setIssuedAt(new Date()) // 토큰 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키와 알고리즘으로 서명
                .compact(); // JWT 토큰 생성
    }

    /**
     * 토큰에서 사용자 ID 추출 메소드
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String extractUserId(String token) {
        return getClaims(token).getSubject(); // 클레임에서 주제(사용자 ID 추출)
    }


    /**
     * 토큰 유효성 검사 메소드
     * @param token JWT 토큰
     * @return 유효한 토큰 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 검증을 위한 비밀키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 및 검증
            logger.debug("Token is valid."); // 유효한 토큰 로그
            return true; // 유효한 토큰
        } catch (JwtException ex) {
            logger.error("JWT validation failed", ex); // 토큰 검증 실패 로그
            return false; // 유효하지 않은 토큰
        }
    }

    /**
     * 토큰에서 클레임 추출
     * @param token JWT 토큰
     * @return 클레임 정보
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 검증을 위한 비밀키 설정
                .build()
                .parseClaimsJws(token) //토큰 파싱 및 클레임 추출
                .getBody(); // 클레임 반환
    }

    /**
     * 토큰에서 역할 정보 추출 메소드
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String extractRoles(String token) {
        Claims claims = getClaims(token); // 클레임 추출
        return claims.get("roles", String.class); // roles 클레임 값 반환
    }
}
