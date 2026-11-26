package com.flio.dto.user;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
}
