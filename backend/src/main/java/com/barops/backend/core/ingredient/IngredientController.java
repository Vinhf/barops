package com.barops.backend.core.ingredient;

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
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INGREDIENT_READ')")
    public List<Ingredient> findAll() {
        return ingredientService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INGREDIENT_READ')")
    public Ingredient findById(@PathVariable UUID id) {
        return ingredientService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INGREDIENT_WRITE')")
    public ResponseEntity<Ingredient> create(@Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INGREDIENT_UPDATE')")
    public Ingredient update(@PathVariable UUID id, @Valid @RequestBody IngredientRequest request) {
        return ingredientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INGREDIENT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
