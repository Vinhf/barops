package com.barops.pos.inventory;

import com.barops.core.tenant.TenantContext;
import com.barops.pos.BranchScopeService;
import com.barops.pos.inventory.dto.BranchStockRequest;
import com.barops.pos.inventory.dto.BranchStockResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BranchStockService {

    private final BranchScopeService branchScopeService;
    private final BranchIngredientStockRepository stockRepository;
    private final IngredientRepository ingredientRepository;

    public BranchStockService(
            BranchScopeService branchScopeService,
            BranchIngredientStockRepository stockRepository,
            IngredientRepository ingredientRepository
    ) {
        this.branchScopeService = branchScopeService;
        this.stockRepository = stockRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional(readOnly = true)
    public List<BranchStockResponse> list(Long branchId) {
        branchScopeService.requireAccessibleBranch(branchId);
        return stockRepository.findAllByBranchIdAndBusinessId(branchId, TenantContext.requireCurrentBusinessId())
                .stream().map(BranchStockResponse::from).toList();
    }

    @Transactional
    public BranchStockResponse create(Long branchId, BranchStockRequest request) {
        var branch = branchScopeService.requireAccessibleBranch(branchId);
        Ingredient ingredient = requireIngredient(request.ingredientId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        if (stockRepository.findByBranchIdAndIngredientIdAndBusinessId(branchId, ingredient.getId(), businessId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyên liệu đã có bản ghi tồn kho tại chi nhánh");
        }

        BranchIngredientStock stock = new BranchIngredientStock();
        stock.setBranchId(branch.getId());
        stock.setIngredient(ingredient);
        stock.setStockQuantity(request.stockQuantity());
        return BranchStockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public BranchStockResponse update(Long branchId, Long stockId, BranchStockRequest request) {
        branchScopeService.requireAccessibleBranch(branchId);
        BranchIngredientStock stock = requireStock(branchId, stockId);
        Ingredient ingredient = requireIngredient(request.ingredientId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        stockRepository.findByBranchIdAndIngredientIdAndBusinessId(branchId, ingredient.getId(), businessId)
                .filter(existing -> !existing.getId().equals(stockId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyên liệu đã có bản ghi tồn kho tại chi nhánh");
                });
        stock.setIngredient(ingredient);
        stock.setStockQuantity(request.stockQuantity());
        return BranchStockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public void delete(Long branchId, Long stockId) {
        branchScopeService.requireAccessibleBranch(branchId);
        BranchIngredientStock stock = requireStock(branchId, stockId);
        if (stock.getStockQuantity().signum() != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ xoá được bản ghi tồn kho khi số lượng bằng 0");
        }
        stockRepository.delete(stock);
    }

    private BranchIngredientStock requireStock(Long branchId, Long stockId) {
        BranchIngredientStock stock = stockRepository.findByIdForCurrentTenant(stockId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi tồn kho không tồn tại"));
        if (!stock.getBranchId().equals(branchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi tồn kho không tồn tại");
        }
        return stock;
    }

    private Ingredient requireIngredient(Long ingredientId) {
        return ingredientRepository.findByIdForCurrentTenant(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nguyên liệu không tồn tại"));
    }
}
