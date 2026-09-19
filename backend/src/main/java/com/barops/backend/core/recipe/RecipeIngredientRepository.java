package com.barops.backend.core.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {
    List<RecipeIngredient> findByRecipe(Recipe recipe);
    void deleteByRecipe(Recipe recipe);
}
