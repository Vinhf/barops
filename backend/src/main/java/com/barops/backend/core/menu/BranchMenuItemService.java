package com.barops.backend.core.menu;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.product.Product;
import com.barops.backend.core.product.ProductRepository;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class BranchMenuItemService {

    private final BranchMenuItemRepository menuRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public BranchMenuItemService(BranchMenuItemRepository menuRepository, BranchRepository branchRepository,
            ProductRepository productRepository) {
        this.menuRepository = menuRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public List<BranchMenuItem> findActive() {
        return menuRepository.findByBranchAndActiveTrue(currentBranch());
    }

    public BranchMenuItem create(BranchMenuItemRequest request) {
        Branch branch = currentBranch();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!product.getBranch().getId().equals(branch.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product does not belong to this branch");
        }

        BranchMenuItem item = new BranchMenuItem();
        item.setBranch(branch);
        item.setProduct(product);
        item.setSellingPrice(request.getSellingPrice() == null ? product.getPrice() : request.getSellingPrice());
        item.setActive(request.isActive());
        return menuRepository.save(item);
    }

    public BranchMenuItem update(UUID id, BranchMenuItemRequest request) {
        BranchMenuItem item = menuRepository.findByIdAndBranch(id, currentBranch())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        item.setSellingPrice(request.getSellingPrice());
        item.setActive(request.isActive());
        return menuRepository.save(item);
    }

    public void delete(UUID id) {
        BranchMenuItem item = menuRepository.findByIdAndBranch(id, currentBranch())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        menuRepository.delete(item);
    }

    private Branch currentBranch() {
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
