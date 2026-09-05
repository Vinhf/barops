package com.barops.core.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long businessId,
        Long branchId, // null nếu là Owner
        String role
) {
}
