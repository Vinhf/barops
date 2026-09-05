package com.barops.core.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Nơi DUY NHẤT tạo và verify JWT trong toàn hệ thống.
 * Không nơi nào khác được tự parse/tự ký JWT — tránh rải rác logic bảo mật
 * (giống nguyên tắc "1 base layer chung" đã áp dụng cho TenantAwareRepository).
 */
@Service
public class JwtService {

    // Claim "type" để phân biệt access token vs refresh token —
    // tránh trường hợp 1 access token bị lộ lại bị dùng để gọi /refresh xin token mới liên tục.
    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_BUSINESS_ID = "businessId";
    private static final String CLAIM_BRANCH_ID = "branchId";
    private static final String CLAIM_ROLE = "role";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey signingKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtService(
            @Value("${barops.jwt.secret}") String secret,
            @Value("${barops.jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes,
            @Value("${barops.jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays
    ) {
        // HS256 cần key tối thiểu 256 bit (32 byte) — secret trong application.yml đã đủ dài.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    /**
     * Access token: sống ngắn, mang đủ businessId + branchId + role để mọi request tự biết
     * tenant/chi nhánh/quyền mà không cần query DB lại.
     *
     * branchId có thể NULL (Owner — không giới hạn theo chi nhánh nào). jjwt tự bỏ qua claim
     * có giá trị null (không nhét "branchId": null vào payload), nên phía đọc (extractBranchId)
     * sẽ trả về null một cách tự nhiên cho Owner — không cần xử lý riêng ở đây.
     */
    public String generateAccessToken(Long userId, Long businessId, Long branchId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_BUSINESS_ID, businessId)
                .claim(CLAIM_BRANCH_ID, branchId)
                .claim(CLAIM_ROLE, role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(accessTokenExpirationMinutes))))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Refresh token: sống dài, CHỈ mang userId — KHÔNG mang businessId/role.
     * Lý do: lúc refresh, server bắt buộc phải tra lại DB lấy role/businessId MỚI NHẤT
     * (phòng trường hợp Owner vừa đổi quyền của user đó), không tin vào claim cũ trong token.
     */
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofDays(refreshTokenExpirationDays))))
                .signWith(signingKey)
                .compact();
    }

    /** Ném JwtException (bao gồm ExpiredJwtException) nếu token sai chữ ký hoặc hết hạn — để caller tự bắt và trả 401. */
    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public Long extractUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public Long extractBusinessId(Claims claims) {
        return claims.get(CLAIM_BUSINESS_ID, Long.class);
    }

    /** Trả về null cho Owner (JWT không có claim này) — đây là điều BÌNH THƯỜNG, không phải lỗi. */
    public Long extractBranchId(Claims claims) {
        return claims.get(CLAIM_BRANCH_ID, Long.class);
    }

    public String extractRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }

    /** Tiện ích để caller phân biệt lỗi "hết hạn" (nên gợi ý refresh) với lỗi "token sai/giả mạo". */
    public boolean isExpired(JwtException ex) {
        return ex instanceof ExpiredJwtException;
    }
}
