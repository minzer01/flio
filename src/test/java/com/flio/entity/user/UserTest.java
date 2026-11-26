package com.flio.entity.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void 탈퇴하면_개인정보는_지우고_상태만_DELETED로_바꾼다() {
        User user = new User("user@example.com", "encoded", "여행자", UserRole.USER);

        user.withdraw();

        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getNickname()).isEqualTo("탈퇴한 사용자");
    }
}
