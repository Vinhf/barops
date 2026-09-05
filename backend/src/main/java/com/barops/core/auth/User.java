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
 *
 * branchId là field THƯỜNG (không dùng BaseBranchEntity) vì được phép NULL:
 *   - null   => Owner, xem/quản lý được tất cả chi nhánh của business.
 *   - có giá trị => Manager/Staff, chỉ thao tác được đúng chi nhánh đó.
 * "Điều chuyển chi nhánh" = update lại field này, chưa cần lưu lịch sử ở Module 1.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseTenantEntity {

    @Column(name = "branch_id")
    private Long branchId;

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
