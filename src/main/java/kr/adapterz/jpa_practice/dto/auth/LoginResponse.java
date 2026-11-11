package kr.adapterz.jpa_practice.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
}
