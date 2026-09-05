package com.barops.core.auth;

/**
 * Giữ branch_id của NGƯỜI ĐANG ĐĂNG NHẬP (từ JWT), KHÔNG phải "branch đang được thao tác".
 * Đây là khác biệt quan trọng so với TenantContext:
 *
 *   - TenantContext.businessId  => tự động ÁP DỤNG cho entity đang tạo/query (mọi user đều
 *                                   thuộc đúng 1 business, không có ngoại lệ).
 *   - CallerBranchContext.branchId => CHỈ dùng để KIỂM TRA quyền, không tự áp cho entity.
 *                                   Vì Owner có branchId = null (quản lý mọi chi nhánh),
 *                                   nên "branch đang thao tác" phải lấy từ request
 *                                   (path variable /api/branches/{branchId}/...), không thể
 *                                   suy ra tự động như business_id.
 *
 * Luồng dùng thực tế: JwtAuthenticationFilter set giá trị này từ claim "branchId" trong JWT
 * (có thể null) → BranchAccessGuard đọc giá trị này để so sánh với branchId người gọi
 * đang cố truy cập (lấy từ URL) → cho qua hoặc chặn 403.
 */
public final class CallerBranchContext {

    private static final ThreadLocal<Long> CALLER_BRANCH_ID = new ThreadLocal<>();

    private CallerBranchContext() {
    }

    /** value = null nghĩa là Owner (không giới hạn theo chi nhánh nào). */
    public static void set(Long branchId) {
        CALLER_BRANCH_ID.set(branchId);
    }

    public static Long get() {
        return CALLER_BRANCH_ID.get();
    }

    /** true nếu người gọi bị giới hạn vào 1 chi nhánh cụ thể (Staff/Manager). */
    public static boolean isRestrictedToOneBranch() {
        return CALLER_BRANCH_ID.get() != null;
    }

    public static void clear() {
        CALLER_BRANCH_ID.remove();
    }
}
