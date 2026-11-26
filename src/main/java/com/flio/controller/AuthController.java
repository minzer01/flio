package com.flio.controller;

import com.flio.dto.auth.LoginRequest;
import com.flio.dto.auth.LoginResponse;
import com.flio.dto.user.CreateUserRequest;
import com.flio.dto.user.UserResponse;
import com.flio.entity.user.User;
import com.flio.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원 가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody CreateUserRequest request) {
        User saved = authService.signup(request);

        URI location = URI.create("/api/v1/users/" + saved.getId());
        return ResponseEntity.created(location).body(UserResponse.of(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse tokens = authService.login(request);
        return ResponseEntity.ok(tokens);
    }



}
