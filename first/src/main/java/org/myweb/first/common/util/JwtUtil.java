package org.myweb.first.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component // 스프링 컴포넌트로 등록: @Autowired를 사용하여 JwtUtil을 주입받을 수 있다
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class); // 로깅을 위한 Logger

    /* JWT 서명에 사용되는 비밀키를 외부 설정 파일에서 관리할 수 있게 함 */
    @Value("${jwt.secret}") // application.properties에서 jwt.secret 값 주입
    private String SECRET_KEY;

    /* Access Token의 만료시간을 외부 설정 파일에서 관리할 수 있게 함*/
    @Value("${jwt.access-token.expiration}") // application.properties에서 jwt.expiration 값 주입
    private long ACCESS_TOKEN_EXPIRATION_TIME; // Access Token 만료 시간(밀리초 단위)

    /* Refresh Token의 만료시간을 외부 설정 파일에서 관리할 수 있게 함*/
    @Value("${jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // Refresh Token 만료 시간

    /* 토큰의 유효성 검증 및 토큰 서명하는데 사용됨*/
    private Key key; // JWT 서명에 사용할 비밀키

    /**
     * `SECRET_KEY`는 Base64로 인코딩된 비밀키다. 이를 Bean 초기화 시 디코딩 및 HMAC SHA 알고리즘의 Key 객체 생성
     * @이 메소드는 스프링이 JwtUtil 빈을 생성한 후 자동으로 호출된다
     * @빈 초기화 시 필요한 설정을 수행하는 데 사용된다.
     */
    @PostConstruct
    public void init() {
        try {
            byte[] decodedSecret = Base64.getDecoder().decode(SECRET_KEY); //Base64로 인코딩된 비밀키를 디코딩하여 원래의 바이트 배열로 변환한다
            this.key = Keys.hmacShaKeyFor(decodedSecret); // HMAX SHA 알고리즘에 적합한 비밀키 생성
            logger.info("JWT Secret Key initialized successfully."); // 초기화 성공 로그
            logger.info("Access Token Expiration Time: {} ms", ACCESS_TOKEN_EXPIRATION_TIME); // Access Token 만료 시간 로그
            logger.info("Refresh Token Expiration Time: {} ms", REFRESH_TOKEN_EXPIRATION_TIME); // Refresh Token 만료 시간 로그
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64-encoded secret key", e); //비밀키 디코딩 오류 로그
            throw e; // 예외 재발생
        }
    }

    /**
     * Access Token 생성 메소드: 사용자 ID와 역할 정보를 포함
     * @param userId 사용자 UUID
     * @param memType 사용자 역할
     * @return 생성된 JWT 토큰
     */
    public String generateAccessToken(String userId, String memType) {
        return Jwts.builder() // JWT 토큰 빌더 생성
                .setSubject(userId) // 토큰의 주제 설정 (사용자 ID)
                .claim("roles", memType) // memType을 roles 클레임으로 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 토큰 만료 시간 설정: 현재 시간 + Access Token 만료 시간(1일)
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키와 HMAC SHA-256 알고리즘으로 토큰에 사명하여 토큰의 무결성과 신뢰성을 보장한다.
                .compact(); // JWT 토큰을 최종적을 생성하여 문자열 형태로 반환한다.
    }

    /**
     * Refresh Token 생성 메소드: 사용자 ID와 역할 정보를 포함
     * @param userId 사용자 ID
     * @param memType 사용자 역할
     * @return 생성된 Refresh Token
     */
    public String generateRefreshToken(String userId, String memType) {
        return Jwts.builder()
                .setSubject(userId) // 토큰의 주제 설정 (사용자 ID)
                .claim("roles", memType) // memType을 roles 클레임으로 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)) // Refresh Token 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키와 HMAC SHA-256 알고리즘으로 토큰 서명
                .compact(); // JWT 토큰을 최종적으로 생성하여 문자열 형태로 반환
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
            Jwts.parserBuilder() // JWT 파서 빌더 생성
                    .setSigningKey(key) // 토큰 서명을 검증하기 위한 비밀키를 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱하고 서명 검증. 이 과정에서 토큰의 무결성, 유효 기간 등이 확인됨
            logger.debug("Token is valid."); // 유효한 토큰 로그
            return true; // 유효한 토큰
        }catch(ExpiredJwtException e){
            log.error("JWT expired: {}", e.getMessage());
            return false; // 만료된 토큰
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
        return Jwts.parserBuilder() // JWT 파서 빌더 생성
                .setSigningKey(key) // 토큰 서명 검증을 위한 비밀키 설정
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
