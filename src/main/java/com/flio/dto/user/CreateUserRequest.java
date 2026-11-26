package com.flio.dto.user;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String password;
    private String nickname;
}
