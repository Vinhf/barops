package com.barops.pos.product;

import com.barops.core.tenant.TenantAwareRepository;

import java.util.List;
import java.util.Optional;

public interface BranchMenuItemRepository extends TenantAwareRepository<BranchMenuItem> {
    List<BranchMenuItem> findAllByBranchIdAndBusinessId(Long branchId, Long businessId);
    Optional<BranchMenuItem> findByBranchIdAndProductIdAndBusinessId(Long branchId, Long productId, Long businessId);
    List<BranchMenuItem> findAllByProductIdAndBusinessId(Long productId, Long businessId);
}
