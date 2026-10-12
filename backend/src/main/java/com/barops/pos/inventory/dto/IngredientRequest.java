package com.barops.pos.inventory.dto;

import com.barops.pos.inventory.IngredientUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IngredientRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull IngredientUnit unit
) {
}
