package com.flio.service;

import com.flio.entity.user.User;
import com.flio.entity.user.UserRole;
import com.flio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    public User getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Transactional
    public User update(Long id, String nickname) {
        User user = findById(id);
        if (nickname != null) {
            user.setNickname(nickname);
        }
        return user;
    }

    @Transactional
    public void withdraw(Long id) { //회원 탈퇴 - 회원 자체 삭제 X
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));
        user.withdraw();
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = findById(id);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
    }
}