package com.barops.pos.inventory;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Định nghĩa nguyên liệu dùng chung trong catalog Business; tồn kho được lưu riêng theo branch.
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

}
