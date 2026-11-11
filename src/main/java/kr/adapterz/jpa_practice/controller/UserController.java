package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.user.CreateUserRequest;
import kr.adapterz.jpa_practice.dto.user.UpdateUserRequest;
import kr.adapterz.jpa_practice.dto.user.UserResponse;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


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
