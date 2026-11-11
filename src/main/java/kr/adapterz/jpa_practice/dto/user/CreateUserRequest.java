package kr.adapterz.jpa_practice.dto.user;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String password;
    private String nickname;
}
