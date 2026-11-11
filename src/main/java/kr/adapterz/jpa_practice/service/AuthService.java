package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.auth.LoginRequest;
import kr.adapterz.jpa_practice.dto.auth.LoginResponse;
import kr.adapterz.jpa_practice.dto.user.CreateUserRequest;
import kr.adapterz.jpa_practice.entity.user.UserRole;
import kr.adapterz.jpa_practice.jwt.TokenProvider;
import kr.adapterz.jpa_practice.repository.UserRepository;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import kr.adapterz.jpa_practice.entity.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Transactional
    public User signup(CreateUserRequest request) {
        //이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), encodedPassword, request.getNickname(), UserRole.USER);
        return userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        //미인증 객체 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        //AuthenticationManager에 넘겨서 객체 인증
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        //인증된 사용자 정보 객체
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        //토큰 생성
        String accessToken = tokenProvider.createAccessToken(principal.getUser());
        String refreshToken = tokenProvider.createRefreshToken(principal.getUser());

        return new LoginResponse(accessToken, refreshToken);
    }
}
