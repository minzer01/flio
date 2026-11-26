package com.flio.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;
}
