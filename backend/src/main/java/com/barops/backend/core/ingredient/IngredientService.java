package com.barops.backend.core.ingredient;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.auth.BranchAccessGuard;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final BranchRepository branchRepository;
    private final BranchAccessGuard branchAccessGuard;

    public IngredientService(IngredientRepository ingredientRepository, BranchRepository branchRepository, BranchAccessGuard branchAccessGuard) {
        this.ingredientRepository = ingredientRepository;
        this.branchRepository = branchRepository;
        this.branchAccessGuard = branchAccessGuard;
    }

    public List<Ingredient> findAll() {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        return ingredientRepository.findByBranch(branch);
    }

    public Ingredient findById(UUID id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

        branchAccessGuard.assertAccess(ingredient.getBranch().getId());
        return ingredient;
    }

    public Ingredient create(IngredientRequest request) {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setUnit(request.getUnit());
        ingredient.setCostPrice(request.getCostPrice());
        ingredient.setBranch(branch);

        return ingredientRepository.save(ingredient);
    }

    public Ingredient update(UUID id, IngredientRequest request) {
        Ingredient ingredient = findById(id);
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setUnit(request.getUnit());
        ingredient.setCostPrice(request.getCostPrice());
        return ingredientRepository.save(ingredient);
    }

    public void delete(UUID id) {
        Ingredient ingredient = findById(id);
        ingredientRepository.delete(ingredient);
    }
}
