package com.barops.pos.product;

import com.barops.core.tenant.TenantAwareRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends TenantAwareRepository<Recipe> {

    List<Recipe> findAllByProductIdAndBusinessId(Long productId, Long businessId);
    Optional<Recipe> findByIdAndProductIdAndBusinessId(Long id, Long productId, Long businessId);
    boolean existsByProductIdAndIngredientIdAndBusinessId(Long productId, Long ingredientId, Long businessId);
    boolean existsByIngredientIdAndBusinessId(Long ingredientId, Long businessId);
}
