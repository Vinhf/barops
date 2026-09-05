package com.barops.core.tenant;

/**
 * Branch chỉ cần lọc theo business_id (1 Business có nhiều Branch) — dùng thẳng
 * TenantAwareRepository, KHÔNG cần BranchAwareRepository vì Branch chính là khái niệm branch,
 * không thuộc về 1 branch nào khác (giống lý do Business không extends BaseTenantEntity).
 */
public interface BranchRepository extends TenantAwareRepository<Branch> {
}
