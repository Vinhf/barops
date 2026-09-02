# Module `pos`

Chưa triển khai — thuộc Sprint 1-3:

- Sprint 1: `Ingredient`, `Product`, `Recipe` (BOM) — CRUD cơ bản
- Sprint 2: `Order`, `OrderItem` + logic trừ kho tự động theo recipe (`@Transactional`,
  xử lý race condition — dùng `SELECT ... FOR UPDATE` hoặc optimistic locking `@Version`
  trên `Ingredient.stockQuantity`)
- Sprint 3: `Table` (trạng thái bàn), thanh toán/discount/split bill/complimentary,
  `PromotionRule` (happy hour theo ngày/giờ)

Tất cả entity trong module này PHẢI extends `BaseTenantEntity`
(xem `com.barops.core.tenant`) và repository PHẢI extends `TenantAwareRepository<T>`.
