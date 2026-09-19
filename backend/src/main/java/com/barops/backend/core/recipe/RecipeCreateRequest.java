package com.barops.backend.core.recipe;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RecipeCreateRequest {

    @NotNull
    @Valid
    private RecipeRequest recipe;

    @NotNull
    private List<@Valid RecipeIngredientRequest> ingredients;

    public RecipeRequest getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeRequest recipe) {
        this.recipe = recipe;
    }

    public List<RecipeIngredientRequest> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }
}
