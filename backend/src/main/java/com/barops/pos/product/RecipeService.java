package com.barops.pos.product;

import com.barops.core.tenant.TenantContext;
import com.barops.pos.inventory.Ingredient;
import com.barops.pos.inventory.IngredientRepository;
import com.barops.pos.product.dto.RecipeRequest;
import com.barops.pos.product.dto.RecipeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecipeService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public RecipeService(
            ProductRepository productRepository,
            IngredientRepository ingredientRepository,
            RecipeRepository recipeRepository
    ) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> list(Long productId) {
        requireProduct(productId);
        Long businessId = TenantContext.requireCurrentBusinessId();
        return recipeRepository.findAllByProductIdAndBusinessId(productId, businessId).stream()
                .map(RecipeResponse::from)
                .toList();
    }

    @Transactional
    public RecipeResponse create(Long productId, RecipeRequest request) {
        Product product = requireProduct(productId);
        Ingredient ingredient = requireIngredient(request.ingredientId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        if (recipeRepository.existsByProductIdAndIngredientIdAndBusinessId(productId, ingredient.getId(), businessId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyên liệu đã có trong công thức");
        }

        Recipe recipe = new Recipe();
        recipe.setProduct(product);
        recipe.setIngredient(ingredient);
        recipe.setQuantity(request.quantity());
        return RecipeResponse.from(recipeRepository.save(recipe));
    }

    @Transactional
    public RecipeResponse update(Long productId, Long recipeId, RecipeRequest request) {
        requireProduct(productId);
        Ingredient ingredient = requireIngredient(request.ingredientId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        Recipe recipe = recipeRepository.findByIdAndProductIdAndBusinessId(recipeId, productId, businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dòng công thức không tồn tại"));
        if (!recipe.getIngredient().getId().equals(ingredient.getId())
                && recipeRepository.existsByProductIdAndIngredientIdAndBusinessId(productId, ingredient.getId(), businessId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyên liệu đã có trong công thức");
        }
        recipe.setIngredient(ingredient);
        recipe.setQuantity(request.quantity());
        return RecipeResponse.from(recipeRepository.save(recipe));
    }

    @Transactional
    public void delete(Long productId, Long recipeId) {
        requireProduct(productId);
        Recipe recipe = recipeRepository.findByIdAndProductIdAndBusinessId(
                        recipeId, productId, TenantContext.requireCurrentBusinessId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dòng công thức không tồn tại"));
        recipeRepository.delete(recipe);
    }

    private Product requireProduct(Long id) {
        Product product = productRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món không tồn tại"));
        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể sửa công thức của món đã ngừng bán");
        }
        return product;
    }

    private Ingredient requireIngredient(Long id) {
        return ingredientRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nguyên liệu không tồn tại"));
    }
}
