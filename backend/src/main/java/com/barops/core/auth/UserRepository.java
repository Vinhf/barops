package com.barops.core.auth;

import com.barops.core.tenant.TenantAwareRepository;

import java.util.Optional;

public interface UserRepository extends TenantAwareRepository<User> {

    /**
     * Dùng lúc LOGIN — lúc này chưa có TenantContext (chưa biết business_id),
     * nên phải query email toàn hệ thống, không qua findAllForCurrentTenant().
     * Đây là NGOẠI LỆ DUY NHẤT được phép query không lọc tenant, vì mục đích
     * của nó chính là để XÁC ĐỊNH tenant.
     */
    Optional<User> findByEmailIgnoreCase(String email);
}
