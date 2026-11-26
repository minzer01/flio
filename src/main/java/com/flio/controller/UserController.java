package com.flio.controller;

import com.flio.dto.user.ChangePasswordRequest;
import com.flio.dto.user.UpdateUserRequest;
import com.flio.dto.user.UserResponse;
import com.flio.entity.user.User;
import com.flio.security.CustomUserDetails;
import com.flio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(UserResponse.of(user));
    }

    // 현재 로그인한 사용자 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateUserRequest request) {
        Long userId = userDetails.getUser().getId();
        User updatedUser = userService.update(userId, request.getNickname());
        return ResponseEntity.ok(UserResponse.of(updatedUser));
    }

    // 현재 로그인한 사용자 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangePasswordRequest request) {
        Long userId = userDetails.getUser().getId();
        userService.changePassword(userId, request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(UserResponse.of(user));// 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();// 404
        }
    }

    // 회원 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User updatedUser = userService.update(id, request.getNickname());
            return ResponseEntity.ok(UserResponse.of(updatedUser));// 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();// 404
        }
    }

    // 회원 탈퇴 - 회원 삭제X
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        userService.withdraw(id);
        return ResponseEntity.noContent().build();// 204
    }
}
