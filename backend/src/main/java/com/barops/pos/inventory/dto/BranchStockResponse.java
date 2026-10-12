package com.barops.pos.inventory.dto;

import com.barops.pos.inventory.BranchIngredientStock;
import com.barops.pos.inventory.IngredientUnit;

import java.math.BigDecimal;

public record BranchStockResponse(
        Long id,
        Long branchId,
        Long ingredientId,
        String ingredientName,
        IngredientUnit unit,
        BigDecimal stockQuantity,
        Long version
) {
    public static BranchStockResponse from(BranchIngredientStock stock) {
        return new BranchStockResponse(
                stock.getId(),
                stock.getBranchId(),
                stock.getIngredient().getId(),
                stock.getIngredient().getName(),
                stock.getIngredient().getUnit(),
                stock.getStockQuantity(),
                stock.getVersion()
        );
    }
}
