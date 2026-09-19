package com.barops.backend.core.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.tenant.Branch;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    List<Recipe> findByBranch(Branch branch);
}
