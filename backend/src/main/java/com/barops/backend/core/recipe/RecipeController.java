package com.barops.backend.core.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RECIPE_READ')")
    public List<Recipe> findAll() {
        return recipeService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RECIPE_READ')")
    public Recipe findById(@PathVariable UUID id) {
        return recipeService.findById(id);
    }

    @GetMapping("/{id}/ingredients")
    @PreAuthorize("hasAuthority('RECIPE_READ')")
    public List<RecipeIngredient> getIngredients(@PathVariable UUID id) {
        return recipeService.getIngredients(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RECIPE_WRITE')")
    public ResponseEntity<Recipe> create(@Valid @RequestBody RecipeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeService.create(request.getRecipe(), request.getIngredients()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RECIPE_UPDATE')")
    public Recipe update(@PathVariable UUID id, @Valid @RequestBody RecipeCreateRequest request) {
        return recipeService.update(id, request.getRecipe(), request.getIngredients());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RECIPE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
