package com.barops.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Mọi repository của entity nghiệp vụ (extends BaseTenantEntity) PHẢI extends interface này
 * thay vì JpaRepository trực tiếp.
 *
 * Cung cấp sẵn các method đã tự động lọc theo business_id của tenant hiện tại (TenantContext) —
 * KHÔNG viết `findByBusinessId(...)` thủ công ở từng repository con.
 *
 * Ví dụ dùng:
 *   public interface ProductRepository extends TenantAwareRepository<Product> {
 *       // các query riêng của Product vẫn viết bình thường,
 *       // nhưng ưu tiên dùng findAllForCurrentTenant()/findByIdForCurrentTenant() bên dưới
 *       // khi cần đảm bảo scope tenant.
 *   }
 */
@NoRepositoryBean
public interface TenantAwareRepository<T extends BaseTenantEntity>
        extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    private Specification<T> currentTenantSpec() {
        Long businessId = TenantContext.requireCurrentBusinessId();
        return (root, query, cb) -> cb.equal(root.get("businessId"), businessId);
    }

    default List<T> findAllForCurrentTenant() {
        return findAll(currentTenantSpec());
    }

    default Optional<T> findByIdForCurrentTenant(Long id) {
        Specification<T> byId = (root, query, cb) -> cb.equal(root.get("id"), id);
        return findOne(currentTenantSpec().and(byId));
    }
}
