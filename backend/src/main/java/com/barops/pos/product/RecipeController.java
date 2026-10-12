package com.barops.pos.product;

import com.barops.pos.product.dto.RecipeRequest;
import com.barops.pos.product.dto.RecipeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<RecipeResponse> list(@PathVariable Long productId) {
        return recipeService.list(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public RecipeResponse create(@PathVariable Long productId, @Valid @RequestBody RecipeRequest request) {
        return recipeService.create(productId, request);
    }

    @PutMapping("/{recipeId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public RecipeResponse update(
            @PathVariable Long productId,
            @PathVariable Long recipeId,
            @Valid @RequestBody RecipeRequest request
    ) {
        return recipeService.update(productId, recipeId, request);
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public void delete(@PathVariable Long productId, @PathVariable Long recipeId) {
        recipeService.delete(productId, recipeId);
    }
}
