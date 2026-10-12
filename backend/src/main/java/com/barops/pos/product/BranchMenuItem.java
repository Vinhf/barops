package com.barops.pos.product;

import com.barops.core.tenant.BaseBranchEntity;
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

@Getter
@Setter
@Entity
@Table(
    name = "branch_menu_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_branch_menu_product",
        columnNames = {"branch_id", "product_id"}
    )
)
public class BranchMenuItem extends BaseBranchEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;
}
