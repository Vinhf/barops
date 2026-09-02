package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

/**
 * MỌI entity nghiệp vụ (Product, Ingredient, Order, Table, ...) PHẢI extends class này.
 * Đây là nơi duy nhất khai báo cột business_id — không copy-paste field này ở từng entity.
 *
 * businessId được set tự động ở @PrePersist từ TenantContext (tenant hiện tại đang đăng nhập),
 * KHÔNG được set thủ công trong code nghiệp vụ để tránh sai sót/giả mạo tenant.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseTenantEntity extends BaseEntity {

    @Column(name = "business_id", nullable = false, updatable = false)
    private Long businessId;

    @PrePersist
    void assignTenantFromContext() {
        if (this.businessId == null) {
            this.businessId = TenantContext.requireCurrentBusinessId();
        }
    }
}
