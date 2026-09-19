package com.barops.backend.core.recipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.auth.BranchAccessGuard;
import com.barops.backend.core.ingredient.Ingredient;
import com.barops.backend.core.ingredient.IngredientRepository;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchAccessGuard branchAccessGuard;

    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            BranchRepository branchRepository,
            IngredientRepository ingredientRepository,
            BranchAccessGuard branchAccessGuard) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchAccessGuard = branchAccessGuard;
    }

    public List<Recipe> findAll() {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        return recipeRepository.findByBranch(branch);
    }

    public Recipe findById(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        branchAccessGuard.assertAccess(recipe.getBranch().getId());
        return recipe;
    }

    @Transactional
    public Recipe create(RecipeRequest recipeRequest, List<RecipeIngredientRequest> ingredients) {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        Recipe recipe = new Recipe();
        recipe.setName(recipeRequest.getName());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setBranch(branch);

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipeIngredientRequest item : ingredients) {
            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

            if (!ingredient.getBranch().getId().equals(branchId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient does not belong to this branch");
            }

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(savedRecipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(item.getQuantity());
            recipeIngredient.setUnit(item.getUnit() == null ? ingredient.getUnit() : item.getUnit());
            recipeIngredients.add(recipeIngredient);
        }

        if (!recipeIngredients.isEmpty()) {
            recipeIngredientRepository.saveAll(recipeIngredients);
        }

        return savedRecipe;
    }

    @Transactional
    public Recipe update(UUID id, RecipeRequest recipeRequest, List<RecipeIngredientRequest> ingredients) {
        Recipe recipe = findById(id);
        recipe.setName(recipeRequest.getName());
        recipe.setDescription(recipeRequest.getDescription());

        recipeIngredientRepository.deleteByRecipe(recipe);

        List<RecipeIngredient> newIngredients = new ArrayList<>();
        for (RecipeIngredientRequest item : ingredients) {
            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

            if (!ingredient.getBranch().getId().equals(recipe.getBranch().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient does not belong to this branch");
            }

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(item.getQuantity());
            recipeIngredient.setUnit(item.getUnit() == null ? ingredient.getUnit() : item.getUnit());
            newIngredients.add(recipeIngredient);
        }

        if (!newIngredients.isEmpty()) {
            recipeIngredientRepository.saveAll(newIngredients);
        }

        return recipeRepository.save(recipe);
    }

    public List<RecipeIngredient> getIngredients(UUID recipeId) {
        Recipe recipe = findById(recipeId);
        return recipeIngredientRepository.findByRecipe(recipe);
    }

    public void delete(UUID id) {
        Recipe recipe = findById(id);
        recipeIngredientRepository.deleteByRecipe(recipe);
        recipeRepository.delete(recipe);
    }
}
