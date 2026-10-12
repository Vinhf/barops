package com.barops.pos.inventory;

import com.barops.core.tenant.TenantContext;
import com.barops.pos.inventory.dto.IngredientRequest;
import com.barops.pos.inventory.dto.IngredientResponse;
import com.barops.pos.product.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final BranchIngredientStockRepository stockRepository;
    private final RecipeRepository recipeRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            BranchIngredientStockRepository stockRepository,
            RecipeRepository recipeRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.stockRepository = stockRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional(readOnly = true)
    public Page<IngredientResponse> list(Pageable pageable) {
        return ingredientRepository.findAllForCurrentTenant(pageable).map(IngredientResponse::from);
    }

    @Transactional
    public IngredientResponse create(IngredientRequest request) {
        Ingredient ingredient = new Ingredient();
        apply(ingredient, request);
        return IngredientResponse.from(ingredientRepository.save(ingredient));
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nguyên liệu không tồn tại"));
        if (ingredient.getUnit() != request.unit()) {
            Long businessId = TenantContext.requireCurrentBusinessId();
            if (recipeRepository.existsByIngredientIdAndBusinessId(id, businessId)
                    || stockRepository.existsByIngredientIdAndBusinessId(id, businessId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Không thể đổi đơn vị khi nguyên liệu đã có trong công thức hoặc tồn kho"
                );
            }
        }
        apply(ingredient, request);
        return IngredientResponse.from(ingredientRepository.save(ingredient));
    }

    @Transactional
    public void delete(Long id) {
        Ingredient ingredient = ingredientRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nguyên liệu không tồn tại"));
        Long businessId = TenantContext.requireCurrentBusinessId();
        if (recipeRepository.existsByIngredientIdAndBusinessId(id, businessId)
                || stockRepository.existsByIngredientIdAndBusinessId(id, businessId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyên liệu đang được dùng trong công thức hoặc tồn kho");
        }
        ingredientRepository.delete(ingredient);
    }

    private void apply(Ingredient ingredient, IngredientRequest request) {
        ingredient.setName(request.name().trim());
        ingredient.setUnit(request.unit());
    }
}
