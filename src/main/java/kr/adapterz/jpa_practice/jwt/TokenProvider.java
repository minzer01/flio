package kr.adapterz.jpa_practice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 14; // 2주

    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes); //서명용 Key 객체 생성
    }

    // 액세스 토큰 생성
    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail())  // 토큰 주체 = 이메일
                .claim("role", user.getUserRole().name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256) //서명해서 문자열로 반환
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 확인
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 → Authentication 변환
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String email = claims.getSubject();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    // Claims 꺼내는 내부 메서드
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
