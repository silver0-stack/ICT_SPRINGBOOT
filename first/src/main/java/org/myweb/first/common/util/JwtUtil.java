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

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // 밀리초 단위

    private Key key;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedSecret = Base64.getDecoder().decode(SECRET_KEY);
            this.key = Keys.hmacShaKeyFor(decodedSecret);
            logger.info("JWT Secret Key initialized successfully.");
            logger.info("JWT Expiration Time: {} ms", EXPIRATION_TIME);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64-encoded secret key", e);
            throw e;
        }
    }

    // 토큰 생성
    public String generateToken(String userId, String roles){
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256) // 올바른 사용
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public String extractUserId(String token){
        return getClaims(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 올바른 사용
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token is valid.");
            return true;
        } catch (JwtException ex) {
            logger.error("JWT validation failed", ex);
            return false;
        }
    }

    // 클레임 추출
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 올바른 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 역할 추출
    public String extractRoles(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", String.class);
    }
}
