package kr.adapterz.jpa_practice.config;

import kr.adapterz.jpa_practice.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .formLogin(form -> form.disable())
                    .httpBasic(basic -> basic.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/login", "/signup",
                                    "/css/**", "/js/**", "/img/**", "/favicon.ico",
                                    "/api/v1/auth/**",   // 로그인/회원가입 API
                                    "/error"
                            ).permitAll()
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/v1/**").authenticated()  // API는 토큰 필요
                            .anyRequest().permitAll()                      // 페이지는 일단 열어두기 -> 추후 수정 가능
                    )

                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
                throws Exception {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }


