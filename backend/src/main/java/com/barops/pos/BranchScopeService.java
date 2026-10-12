package com.barops.pos;

import com.barops.core.auth.BranchAccessGuard;
import com.barops.core.tenant.Branch;
import com.barops.core.tenant.BranchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BranchScopeService {

    private final BranchAccessGuard branchAccessGuard;
    private final BranchRepository branchRepository;

    public BranchScopeService(BranchAccessGuard branchAccessGuard, BranchRepository branchRepository) {
        this.branchAccessGuard = branchAccessGuard;
        this.branchRepository = branchRepository;
    }

    @Transactional(readOnly = true)
    public Branch requireAccessibleBranch(Long branchId) {
        branchAccessGuard.assertAccess(branchId);
        return branchRepository.findByIdForCurrentTenant(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi nhánh không tồn tại"));
    }
}
