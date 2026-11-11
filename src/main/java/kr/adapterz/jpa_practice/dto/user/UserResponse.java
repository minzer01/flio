package kr.adapterz.jpa_practice.dto.user;

import kr.adapterz.jpa_practice.entity.user.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;

    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    public UserResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
