package com.barops.core.tenant;

/**
 * Giữ business_id của tenant hiện tại trong suốt 1 request (ThreadLocal).
 *
 * Được set ở TenantContextFilter (Sprint 1, sau khi có JWT/Auth) — đọc business_id
 * từ user đã đăng nhập, KHÔNG bao giờ đọc từ request param/body do client gửi lên
 * (client có thể giả mạo business_id của tenant khác => lộ dữ liệu chéo tenant).
 *
 * Mọi filter theo tenant (repository, query) đều đi qua class này,
 * không tự viết business_id rải rác ở service layer.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_BUSINESS_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentBusinessId(Long businessId) {
        CURRENT_BUSINESS_ID.set(businessId);
    }

    public static Long getCurrentBusinessId() {
        return CURRENT_BUSINESS_ID.get();
    }

    public static Long requireCurrentBusinessId() {
        Long id = CURRENT_BUSINESS_ID.get();
        if (id == null) {
            throw new IllegalStateException(
                "Không xác định được tenant hiện tại. " +
                "TenantContext phải được set (qua TenantContextFilter) trước khi tạo/đọc entity nghiệp vụ."
            );
        }
        return id;
    }

    /** Gọi ở cuối mỗi request (finally block trong filter) để tránh leak sang thread khác (thread pool tái sử dụng). */
    public static void clear() {
        CURRENT_BUSINESS_ID.remove();
    }
}
