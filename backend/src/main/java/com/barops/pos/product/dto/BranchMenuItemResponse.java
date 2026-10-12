package com.barops.pos.product.dto;

import com.barops.pos.product.BranchMenuItem;

import java.math.BigDecimal;

public record BranchMenuItemResponse(
        Long id,
        Long branchId,
        Long productId,
        String productName,
        BigDecimal price,
        boolean available
) {
    public static BranchMenuItemResponse from(BranchMenuItem item) {
        return new BranchMenuItemResponse(
                item.getId(),
                item.getBranchId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getPrice(),
                item.isAvailable()
        );
    }
}
