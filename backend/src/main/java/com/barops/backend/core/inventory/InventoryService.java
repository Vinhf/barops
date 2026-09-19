package com.barops.backend.core.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.ingredient.Ingredient;
import com.barops.backend.core.ingredient.IngredientRepository;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class InventoryService {

    private final BranchIngredientStockRepository stockRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;

    public InventoryService(BranchIngredientStockRepository stockRepository, BranchRepository branchRepository,
            IngredientRepository ingredientRepository) {
        this.stockRepository = stockRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<BranchIngredientStock> findAll() {
        return stockRepository.findByBranch(getCurrentBranch());
    }

    @Transactional
    public BranchIngredientStock adjust(UUID ingredientId, StockAdjustmentRequest request) {
        Branch branch = getCurrentBranch();
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

        if (!ingredient.getBranch().getId().equals(branch.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ingredient does not belong to this branch");
        }

        BranchIngredientStock stock = stockRepository.findByBranchAndIngredient(branch, ingredient)
                .orElseGet(() -> createStock(branch, ingredient));

        BigDecimal amount = request.getQuantity();
        BigDecimal newQuantity;
        switch (request.getOperation().toUpperCase()) {
            case "IN" -> newQuantity = stock.getQuantity().add(amount);
            case "OUT" -> newQuantity = stock.getQuantity().subtract(amount);
            case "SET" -> newQuantity = amount;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be IN, OUT, or SET");
        }

        if (newQuantity.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative");
        }

        stock.setQuantity(newQuantity);
        return stockRepository.save(stock);
    }

    private BranchIngredientStock createStock(Branch branch, Ingredient ingredient) {
        BranchIngredientStock stock = new BranchIngredientStock();
        stock.setBranch(branch);
        stock.setIngredient(ingredient);
        stock.setQuantity(BigDecimal.ZERO);
        stock.setMinimumQuantity(BigDecimal.ZERO);
        return stock;
    }

    private Branch getCurrentBranch() {
        UUID branchId = TenantContext.getCurrentBranchId();
        UUID businessId = TenantContext.getCurrentBusinessId();

        if (branchId == null || businessId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        if (!branch.getBusiness().getId().equals(businessId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch does not belong to this business");
        }

        return branch;
    }
}
