package com.barops.core.auth;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Nhân viên/chủ quán. Extends BaseTenantEntity => tự động có business_id.
 * Logic đăng nhập, JWT, PasswordEncoder sẽ làm ở Sprint 1.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseTenantEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;
}
