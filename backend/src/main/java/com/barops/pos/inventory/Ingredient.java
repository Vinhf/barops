package com.barops.pos.inventory;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Nguyên liệu trong kho. `stockQuantity` là số lượng tồn hiện tại.
 *
 * `@Version` (optimistic locking) chuẩn bị sẵn cho Sprint 2 — khi nhiều order cùng lúc
 * trừ kho trên cùng 1 ingredient (race condition), Hibernate sẽ tự phát hiện xung đột
 * qua cột `version` và ném OptimisticLockException thay vì lặng lẽ ghi đè sai số liệu.
 * Đây là 1 trong 2 cách xử lý race condition nêu trong doc (cách kia là SELECT ... FOR UPDATE).
 */
@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientUnit unit;

    @Column(name = "stock_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @Version
    private Long version;
}
