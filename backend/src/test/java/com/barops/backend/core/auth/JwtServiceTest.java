package com.barops.backend.core.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L);
    }

    @Test
    void tokenContainsRolePermissionsAndTenantClaims() {
        UUID businessId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        User user = new User();
        user.setUsername("manager");
        user.setRole(Role.MANAGER);
        user.setBusinessId(businessId);
        user.setBranchId(branchId);

        String token = jwtService.generateToken(user);
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("manager");
        assertThat(claims.get("role", String.class)).isEqualTo("MANAGER");
        assertThat(claims.get("businessId", String.class)).isEqualTo(businessId.toString());
        assertThat(claims.get("branchId", String.class)).isEqualTo(branchId.toString());
        assertThat(claims.get("authorities", java.util.List.class))
                .contains("PRODUCT_READ", "RECIPE_UPDATE", "INVENTORY_WRITE")
                .doesNotContain("PRODUCT_DELETE");
    }
}