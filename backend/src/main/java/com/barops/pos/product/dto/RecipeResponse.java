package com.barops.pos.product.dto;

import com.barops.pos.product.Recipe;

import java.math.BigDecimal;

public record RecipeResponse(
        Long id,
        Long productId,
        Long ingredientId,
        String ingredientName,
        String unit,
        BigDecimal quantity
) {
    public static RecipeResponse from(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getProduct().getId(),
                recipe.getIngredient().getId(),
                recipe.getIngredient().getName(),
                recipe.getIngredient().getUnit().name(),
                recipe.getQuantity()
        );
    }
}
