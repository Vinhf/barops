package com.barops.backend.core.auth;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.barops.backend.core.tenant.TenantContext;

@Component
public class BranchAccessGuard {

    public void assertAccess(UUID branchId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        UUID currentBranchId = TenantContext.getCurrentBranchId();
        if (currentBranchId == null || !currentBranchId.equals(branchId)) {
            throw new AccessDeniedException("You do not have access to this branch");
        }
    }

    public boolean hasBusinessAccess(UUID businessId) {
        UUID currentBusinessId = TenantContext.getCurrentBusinessId();
        return currentBusinessId != null && currentBusinessId.equals(businessId);
    }

    public boolean hasBranchAccess(UUID branchId) {
        UUID currentBranchId = TenantContext.getCurrentBranchId();
        return currentBranchId != null && currentBranchId.equals(branchId);
    }
}
