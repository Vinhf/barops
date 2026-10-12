package com.barops.pos.product.dto;

import com.barops.pos.product.Product;

public record ProductResponse(Long id, String name, String displayNameEn, boolean active) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDisplayNameEn(), product.isActive());
    }
}
