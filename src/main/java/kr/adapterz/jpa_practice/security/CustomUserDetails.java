package kr.adapterz.jpa_practice.security;

import kr.adapterz.jpa_practice.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + user.getUserRole().name(); // USER -> ROLE_USER, ADMIN -> ROLE_ADMIN
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 기능 안 쓸 거면 true 고정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 기능 안 쓰면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 기능 안 쓰면 true
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성/비활성 기능 안 쓰면 true
    }
}
