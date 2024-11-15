package org.myweb.first.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

/**
 * JWT 비밀키를 생성하고 Base64로 인코딩하는 유틸리티 클래스
 * 실행 시 콘솔에 Base64로 인코딩된 비밀키 출력
 */
public class JwtSecretGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Base64 Secret Key: " + base64Key);
    }
}
