package com.barops.pos.inventory;

import com.barops.core.tenant.TenantAwareRepository;

import java.util.List;
import java.util.Optional;

public interface BranchIngredientStockRepository extends TenantAwareRepository<BranchIngredientStock> {
    List<BranchIngredientStock> findAllByBranchIdAndBusinessId(Long branchId, Long businessId);
    Optional<BranchIngredientStock> findByBranchIdAndIngredientIdAndBusinessId(
            Long branchId, Long ingredientId, Long businessId);
    boolean existsByIngredientIdAndBusinessId(Long ingredientId, Long businessId);
}
