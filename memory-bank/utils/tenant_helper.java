package com.barops.memorybank.utils;

import com.barops.core.auth.BranchAccessGuard;
import com.barops.core.tenant.TenantContext;

public class TenantHelper {
    public static void enforceTenantIsolation() {
        BranchAccessGuard.assertAccess(TenantContext.getBranchId());
    }
}