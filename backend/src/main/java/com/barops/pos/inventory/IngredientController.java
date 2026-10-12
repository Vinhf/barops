package com.barops.pos.inventory;

import com.barops.pos.inventory.dto.IngredientRequest;
import com.barops.pos.inventory.dto.IngredientResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public Page<IngredientResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ingredientService.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public IngredientResponse create(@Valid @RequestBody IngredientRequest request) {
        return ingredientService.create(request);
    }

    @PutMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public IngredientResponse update(@PathVariable Long ingredientId, @Valid @RequestBody IngredientRequest request) {
        return ingredientService.update(ingredientId, request);
    }

    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public void delete(@PathVariable Long ingredientId) {
        ingredientService.delete(ingredientId);
    }
}
