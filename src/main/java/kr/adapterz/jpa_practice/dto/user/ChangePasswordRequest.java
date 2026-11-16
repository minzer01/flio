package kr.adapterz.jpa_practice.dto.user;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
}
