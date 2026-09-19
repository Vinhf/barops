package com.barops.backend.core.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.ingredient.Ingredient;
import com.barops.backend.core.tenant.Branch;

public interface BranchIngredientStockRepository extends JpaRepository<BranchIngredientStock, UUID> {
    List<BranchIngredientStock> findByBranch(Branch branch);
    Optional<BranchIngredientStock> findByBranchAndIngredient(Branch branch, Ingredient ingredient);
}
