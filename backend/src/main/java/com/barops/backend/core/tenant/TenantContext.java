package com.barops.backend.core.tenant;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> CURRENT_BUSINESS_ID = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_BRANCH_ID = new ThreadLocal<>();

    public static void setCurrentBusinessId(UUID businessId) {
        CURRENT_BUSINESS_ID.set(businessId);
    }

    public static UUID getCurrentBusinessId() {
        return CURRENT_BUSINESS_ID.get();
    }

    public static void clearBusinessId() {
        CURRENT_BUSINESS_ID.remove();
    }

    public static void setCurrentBranchId(UUID branchId) {
        CURRENT_BRANCH_ID.set(branchId);
    }

    public static UUID getCurrentBranchId() {
        return CURRENT_BRANCH_ID.get();
    }

    public static void clearBranchId() {
        CURRENT_BRANCH_ID.remove();
    }
}
