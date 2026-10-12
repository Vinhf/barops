package com.barops.pos.product;

import com.barops.core.tenant.BaseTenantEntity;
import com.barops.pos.inventory.Ingredient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 1 dòng trong công thức (BOM) của Product: cần bao nhiêu Ingredient để làm ra 1 Product.
 * Công thức thuộc catalog của một Business và được dùng chung ở các chi nhánh.
 */
@Getter
@Setter
@Entity
@Table(
    name = "recipes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_recipe_product_ingredient",
        columnNames = {"product_id", "ingredient_id"}
    )
)
public class Recipe extends BaseTenantEntity {

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
