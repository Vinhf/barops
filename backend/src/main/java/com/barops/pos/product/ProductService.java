package com.barops.pos.product;

import com.barops.core.tenant.TenantContext;
import com.barops.pos.product.dto.ProductRequest;
import com.barops.pos.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BranchMenuItemRepository menuRepository;

    public ProductService(ProductRepository productRepository, BranchMenuItemRepository menuRepository) {
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Pageable pageable) {
        return productRepository.findAllForCurrentTenant(pageable).map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món không tồn tại"));
        boolean wasActive = product.isActive();
        apply(product, request);
        product = productRepository.save(product);
        if (wasActive && !product.isActive()) {
            deactivateMenuItems(product.getId());
        }
        return ProductResponse.from(product);
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = productRepository.findByIdForCurrentTenant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món không tồn tại"));
        product.setActive(false);
        productRepository.save(product);
        deactivateMenuItems(product.getId());
    }

    private void deactivateMenuItems(Long productId) {
        menuRepository.findAllByProductIdAndBusinessId(
                        productId, TenantContext.requireCurrentBusinessId())
                .forEach(item -> item.setAvailable(false));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDisplayNameEn(request.displayNameEn() == null ? null : request.displayNameEn().trim());
        if (request.active() != null || product.getId() == null) {
            product.setActive(request.active() == null || request.active());
        }
    }
}
