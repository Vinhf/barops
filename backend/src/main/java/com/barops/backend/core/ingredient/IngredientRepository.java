package com.barops.backend.core.ingredient;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.tenant.Branch;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    List<Ingredient> findByBranch(Branch branch);
}
