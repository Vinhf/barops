package com.barops.pos.product;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Món/đồ uống bán ra. `isActive = false` dùng để "xoá mềm" (soft delete) thay vì xoá cứng —
 * tránh phá vỡ dữ liệu OrderItem lịch sử đã tham chiếu tới product này.
 */
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "display_name_en")
    private String displayNameEn;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
