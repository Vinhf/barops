package com.barops.core.config;

import com.barops.core.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Cấu hình bảo mật thật cho toàn hệ thống (đã thay bản permitAll() tạm thời của Sprint 0).
 *
 * @EnableMethodSecurity bật @PreAuthorize trên controller (UserController dùng để RBAC).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /** BCrypt — xem giải thích chi tiết trong bước "Password phải hash trước" đã trao đổi. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            // STATELESS: server không lưu session nào — mọi thông tin nằm trong JWT client gửi lên mỗi request.
            // Đây là điều kiện bắt buộc để scale nhiều instance backend sau này mà không cần đồng bộ session.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // /api/auth/** (register/login/refresh) mở công khai — lúc gọi các endpoint này
                // client CHƯA CÓ token, nên không thể yêu cầu authenticated().
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                // Swagger UI + OpenAPI JSON — mở public để xem docs được, nhưng gọi thử API
                // NGHIỆP VỤ qua nút "Try it out" vẫn cần bấm "Authorize" nhập Bearer token trước.
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            // Chạy JwtAuthenticationFilter TRƯỚC filter xử lý form-login mặc định của Spring —
            // vì hệ thống này không dùng form-login/session, toàn bộ xác thực dựa trên JWT.
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
