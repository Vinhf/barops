package com.barops.pos.inventory;

import com.barops.core.tenant.BaseBranchEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
    name = "branch_ingredient_stocks",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_branch_ingredient_stock",
        columnNames = {"branch_id", "ingredient_id"}
    )
)
public class BranchIngredientStock extends BaseBranchEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "stock_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @Version
    private Long version;
}
