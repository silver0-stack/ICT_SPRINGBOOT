package org.myweb.first.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.Date;

@Component
public class JwtUtil {

    /*잘못된 @Value 어노테이션 임포트
    * Spring Framework의 @Value 어노테이션을 사용하여 application.properties */
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; //밀리초 단위

    // 토큰 생성
    public String generateToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public String extractUserId(String token){
        return getClaims(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    // 클레임 추출
    // TODO: 클레임이 뭔가요? 토큰을 가지고 있는 BODY?
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
















