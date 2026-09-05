package com.barops.pos.product;

import com.barops.core.tenant.BaseEntity;
import com.barops.pos.inventory.Ingredient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 1 dòng trong công thức (BOM) của Product: cần bao nhiêu Ingredient để làm ra 1 Product.
 * Extends BaseEntity (KHÔNG phải BaseTenantEntity) — vì Recipe không có business_id riêng,
 * tenant của nó suy ra qua Product/Ingredient (cả 2 phải cùng business_id, validate ở service layer).
 */
@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    /** Định lượng ingredient cần cho 1 đơn vị product (cùng unit với Ingredient.unit). */
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;
}
