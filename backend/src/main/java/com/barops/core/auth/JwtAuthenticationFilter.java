package com.barops.core.auth;

import com.barops.core.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Chạy đúng 1 lần cho mỗi request (OncePerRequestFilter), TRƯỚC UsernamePasswordAuthenticationFilter
 * (đăng ký trong SecurityConfig). Làm 2 việc trong 1 lần parse JWT — không tách 2 filter riêng
 * vì tốn công parse token 2 lần cho cùng 1 request:
 *
 *   1) Set SecurityContextHolder -> Spring Security biết "ai đang gọi, quyền gì" -> @PreAuthorize hoạt động.
 *   2) Set TenantContext -> BaseTenantEntity/TenantAwareRepository biết đang thao tác cho business nào.
 *
 * Nếu không có token hoặc token invalid: KHÔNG chặn ở đây, chỉ đơn giản không set Authentication —
 * để SecurityConfig (anyRequest().authenticated()) tự trả 401 cho các endpoint cần bảo vệ.
 * Endpoint public (/api/auth/**) thì không cần Authentication nên vẫn đi tiếp bình thường.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // /api/auth/** (register/login/refresh) chưa thể có token hợp lệ vì đó chính là nơi CẤP token.
        return request.getServletPath().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                Claims claims = jwtService.parseClaims(token);

                // Chặn dùng nhầm refresh token để gọi API nghiệp vụ — refresh token CHỈ được
                // dùng ở đúng 1 chỗ là /api/auth/refresh, nơi đó tự parse riêng, không qua filter này.
                if (jwtService.isAccessToken(claims)) {
                    Long userId = jwtService.extractUserId(claims);
                    Long businessId = jwtService.extractBusinessId(claims);
                    Long branchId = jwtService.extractBranchId(claims); // null nếu Owner — bình thường
                    String role = jwtService.extractRole(claims);

                    List<GrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    TenantContext.setCurrentBusinessId(businessId);
                    CallerBranchContext.set(branchId);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            // Token sai chữ ký / hết hạn / malformed -> không set Authentication.
            // Request đi tiếp nhưng sẽ bị SecurityConfig chặn 401 nếu endpoint cần đăng nhập.
            filterChain.doFilter(request, response);
        } finally {
            // BẮT BUỘC clear cả 2 ở finally — nếu không, thread trong connection pool bị tái sử dụng
            // có thể mang businessId/branchId của request TRƯỚC sang request SAU (lỗi rất khó phát hiện).
            TenantContext.clear();
            CallerBranchContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
