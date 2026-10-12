package com.barops.pos.inventory.dto;

import com.barops.pos.inventory.Ingredient;
import com.barops.pos.inventory.IngredientUnit;

public record IngredientResponse(Long id, String name, IngredientUnit unit) {
    public static IngredientResponse from(Ingredient ingredient) {
        return new IngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getUnit());
    }
}
