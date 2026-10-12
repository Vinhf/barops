package com.barops.pos.product;

import com.barops.core.tenant.TenantContext;
import com.barops.pos.BranchScopeService;
import com.barops.pos.product.dto.BranchMenuItemRequest;
import com.barops.pos.product.dto.BranchMenuItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BranchMenuService {

    private final BranchScopeService branchScopeService;
    private final BranchMenuItemRepository menuRepository;
    private final ProductRepository productRepository;

    public BranchMenuService(
            BranchScopeService branchScopeService,
            BranchMenuItemRepository menuRepository,
            ProductRepository productRepository
    ) {
        this.branchScopeService = branchScopeService;
        this.menuRepository = menuRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<BranchMenuItemResponse> list(Long branchId) {
        branchScopeService.requireAccessibleBranch(branchId);
        return menuRepository.findAllByBranchIdAndBusinessId(branchId, TenantContext.requireCurrentBusinessId())
                .stream().map(BranchMenuItemResponse::from).toList();
    }

    @Transactional
    public BranchMenuItemResponse create(Long branchId, BranchMenuItemRequest request) {
        var branch = branchScopeService.requireAccessibleBranch(branchId);
        Product product = requireActiveProduct(request.productId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        if (menuRepository.findByBranchIdAndProductIdAndBusinessId(branchId, product.getId(), businessId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Món đã có trong menu chi nhánh");
        }

        BranchMenuItem item = new BranchMenuItem();
        item.setBranchId(branch.getId());
        item.setProduct(product);
        apply(item, request);
        return BranchMenuItemResponse.from(menuRepository.save(item));
    }

    @Transactional
    public BranchMenuItemResponse update(Long branchId, Long itemId, BranchMenuItemRequest request) {
        branchScopeService.requireAccessibleBranch(branchId);
        BranchMenuItem item = requireMenuItem(branchId, itemId);
        Product product = requireActiveProduct(request.productId());
        Long businessId = TenantContext.requireCurrentBusinessId();
        menuRepository.findByBranchIdAndProductIdAndBusinessId(branchId, product.getId(), businessId)
                .filter(existing -> !existing.getId().equals(itemId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Món đã có trong menu chi nhánh");
                });
        item.setProduct(product);
        apply(item, request);
        return BranchMenuItemResponse.from(menuRepository.save(item));
    }

    @Transactional
    public void delete(Long branchId, Long itemId) {
        branchScopeService.requireAccessibleBranch(branchId);
        menuRepository.delete(requireMenuItem(branchId, itemId));
    }

    private BranchMenuItem requireMenuItem(Long branchId, Long itemId) {
        BranchMenuItem item = menuRepository.findByIdForCurrentTenant(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món trong menu không tồn tại"));
        if (!item.getBranchId().equals(branchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Món trong menu không tồn tại");
        }
        return item;
    }

    private Product requireActiveProduct(Long productId) {
        Product product = productRepository.findByIdForCurrentTenant(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món không tồn tại"));
        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể đưa món đã ngừng bán vào menu");
        }
        return product;
    }

    private void apply(BranchMenuItem item, BranchMenuItemRequest request) {
        item.setPrice(request.price());
        if (request.available() != null || item.getId() == null) {
            item.setAvailable(request.available() == null || request.available());
        }
    }
}
