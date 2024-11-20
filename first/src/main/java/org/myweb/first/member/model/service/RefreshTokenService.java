package org.myweb.first.member.model.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    // Refresh Token 을 저장할 저장ㅅ오 (예: 데이터벵스, Redis 등)
    // 여기서는 예시로 간단히 메모리 저장소를 사용
    private Map<String, String> refreshTokenStore= new ConcurrentHashMap<>();

    /**
     * Refresh Token 저장
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token
     */
    public void storedRefreshToken(String userId, String refreshToken){
        refreshTokenStore.put(userId, refreshToken);
    }


    /**
     * Refresh Token 검증
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token
     * @return 유효한 토큰 여부
     */
    public boolean validateRefreshToken(String userId, String refreshToken){
        String storedToken = refreshTokenStore.get(userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }


    /**
     * Refresh Token 삭제 (예: 로그아웃)
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(String userId){
        refreshTokenStore.remove(userId);
    }

}
