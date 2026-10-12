package com.barops.pos.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BranchStockRequest(
        @NotNull Long ingredientId,
        @NotNull @DecimalMin("0.000") @Digits(integer = 9, fraction = 3) BigDecimal stockQuantity
) {
}
