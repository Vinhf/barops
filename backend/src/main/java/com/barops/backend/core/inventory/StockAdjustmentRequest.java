package com.barops.backend.core.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class StockAdjustmentRequest {

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;

    @NotBlank
    private String operation;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
