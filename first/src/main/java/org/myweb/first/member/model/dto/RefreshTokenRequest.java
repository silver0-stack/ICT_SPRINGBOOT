package org.myweb.first.member.model.dto;

import lombok.Data;

/**
 * Refresh Token 요청 DTO
 */
@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
