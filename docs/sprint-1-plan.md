> **Cập nhật sau khi đổi hướng sang mô hình 1 Business — nhiều Branch** (xem `docs/ERD.md`):
> Auth giờ phải mang thêm `branch_id` (nullable) trong JWT, và cần thêm `BranchAccessGuard`
> để chặn Staff/Manager thao tác nhầm chi nhánh khác. CRUD Ingredient/Product/Recipe tách
> thành catalog (Business) + BranchMenuItem/BranchIngredientStock (Branch) — xem Giai đoạn B mới.

# Sprint 1 — Auth + CRUD Ingredient/Product/Recipe (2 tuần)

> Theo mục 7 (Sprint Plan) trong project setup doc: "Auth (Owner/Manager/Staff, gắn `business_id`), CRUD Ingredient/Product/Recipe".

## Mục tiêu cuối sprint (Definition of Done riêng cho Sprint 1)
- [ ] Đăng ký + đăng nhập được, nhận về JWT access + refresh token
- [ ] JWT chứa `business_id` + `role`, mọi request sau đó tự xác định đúng tenant qua `TenantContext`
- [ ] Phân quyền hoạt động: Staff không gọi được endpoint chỉ dành cho Owner/Manager
- [ ] CRUD đầy đủ cho Ingredient, Product, Recipe — có validate, có phân trang cơ bản
- [ ] Test: tạo 2 business demo, xác nhận Business A không thấy được Ingredient/Product của Business B (đây là test quan trọng nhất — bảo mật multi-tenant)
- [ ] Postman collection hoặc file `.http` để demo/test tay được

---

## Thứ tự làm việc (khuyến nghị theo đúng dependency, đừng nhảy cóc)

### Giai đoạn A — Auth nền tảng (khoảng 4-5 ngày đầu)
Đây là phần khó nhất vì mọi thứ sau đều phụ thuộc vào nó (không có Auth đúng thì không test được tenant isolation).

1. **Password hashing**: thêm `PasswordEncoder` bean (BCrypt) vào `SecurityConfig`.
2. **DTO đăng ký/đăng nhập**: `RegisterRequest` (email, password, businessName — vì Owner đăng ký = tạo luôn Business mới), `LoginRequest` (email, password), `AuthResponse` (accessToken, refreshToken).
3. **JwtService**: class tạo/verify JWT bằng thư viện `jjwt` đã có sẵn trong `pom.xml`. Token phải nhúng 2 claim quan trọng: `businessId` và `role` — đây là lý do JWT quan trọng hơn session thường trong hệ multi-tenant, vì mọi request sau này tự biết tenant mà không cần query DB lại.
4. **AuthController**: `POST /api/auth/register` (tạo Business mới + User đầu tiên với role OWNER), `POST /api/auth/login` (verify password, trả JWT).
   - Lưu ý: `register` là **ngoại lệ duy nhất** tạo Business — sau khi có Owner rồi, tạo thêm Staff/Manager phải qua endpoint riêng (`POST /api/users`, chỉ Owner/Manager gọi được) chứ không qua `register` nữa.
5. **TenantContextFilter** (`OncePerRequestFilter`): đọc JWT từ header `Authorization: Bearer ...`, giải mã, gọi `TenantContext.setCurrentBusinessId(...)`. Nhớ `TenantContext.clear()` ở `finally` để không leak sang request khác.
6. **Cập nhật `SecurityConfig`**: thay `permitAll()` tạm thời bằng cấu hình thật — `/api/auth/**` mở công khai, còn lại yêu cầu JWT hợp lệ. Đăng ký `TenantContextFilter` chạy trước `UsernamePasswordAuthenticationFilter`.
7. **Phân quyền theo Role**: dùng `@PreAuthorize("hasRole('OWNER')")` hoặc tương đương trên các endpoint quản trị (tạo/xoá user, xoá product...).

### Giai đoạn B — CRUD nghiệp vụ (khoảng 4-5 ngày tiếp)
Làm nhanh hơn Auth nhiều vì đã có `TenantAwareRepository` làm sẵn từ Sprint 0.

8. **Ingredient**: entity (`extends BaseTenantEntity`, fields: `name`, `unit` enum hoặc string ml/g/cái, `stockQuantity` — dùng `NUMERIC`/`BigDecimal`, KHÔNG dùng `double` vì sai số). Repository, Service, Controller (`GET/POST/PUT/DELETE /api/ingredients`).
9. **Product**: entity (`name`, `displayNameEn`, `price` là `BigDecimal`, `isActive` boolean mặc định true). CRUD tương tự.
10. **Recipe (BOM)**: entity nối `Product` — `Ingredient` kèm `quantity`. Đây là bảng quan hệ N-N có thêm dữ liệu (`quantity`) nên phải là entity riêng, không dùng `@ManyToMany` thuần. Endpoint: `POST /api/products/{id}/recipe` (thêm dòng recipe), `GET /api/products/{id}/recipe` (xem đầy đủ BOM của 1 món).
11. **Validation**: dùng `@Valid` + Bean Validation (`@NotBlank`, `@Positive`...) trên DTO request, không validate trực tiếp trên entity.
12. **Exception handling chung**: 1 `@RestControllerAdvice` xử lý lỗi validate/not-found trả về JSON lỗi thống nhất (chưa có ở Sprint 0, nên làm ngay đầu giai đoạn B để dùng xuyên suốt).

### Giai đoạn C — Test + đóng sprint (2-3 ngày cuối)
13. **Test tenant isolation** (quan trọng nhất, đừng bỏ qua): script hoặc test tự động — tạo Business A + B qua `/api/auth/register`, đăng nhập từng bên, tạo Ingredient ở A, gọi API bằng token của B, xác nhận **không thấy** Ingredient đó.
14. **Integration test** luồng Auth: register → login → gọi 1 endpoint cần token → xác nhận 401 nếu không có token.
15. Postman collection hoặc file `.http` trong VS Code (extension REST Client) lưu lại các request mẫu — dùng để Sprint Review demo nhanh bằng Postman như doc mục 8 đã định.
16. Daily note + Sprint Retro theo đúng quy trình mục 8.

---

## Gợi ý chia GitHub Issues (5-8 issue theo Sprint Planning trong doc mục 8)

| # | Issue | Label |
|---|---|---|
| 1 | Auth: PasswordEncoder + JwtService + register/login endpoint | `feature`, `P0` |
| 2 | Auth: TenantContextFilter + SecurityConfig thật (thay permitAll) | `feature`, `P0` |
| 3 | Auth: phân quyền theo Role (Owner/Manager/Staff) | `feature`, `P1` |
| 4 | CRUD Ingredient | `feature`, `P1` |
| 5 | CRUD Product | `feature`, `P1` |
| 6 | Recipe (BOM) — nối Product-Ingredient | `feature`, `P0` (vì Sprint 2 trừ kho phụ thuộc thẳng vào cái này) |
| 7 | Exception handling chung (`@RestControllerAdvice`) | `chore`, `P1` |
| 8 | Test tenant isolation + Postman collection | `chore`, `P0` |

Mỗi issue → tạo branch `feature/<tên>` → PR → merge, đúng flow trong doc mục 8.

---

## Vì sao thứ tự này (không đảo được)
- Auth phải xong trước vì `TenantContext` cần có JWT hợp lệ mới set được `business_id` — CRUD Ingredient/Product không test tenant isolation được nếu chưa có 2 tài khoản 2 business thật.
- Recipe phải xong trong Sprint 1 (không dời sang Sprint 2) vì Sprint 2 (logic trừ kho tự động) đọc trực tiếp từ bảng Recipe để biết trừ ingredient nào bao nhiêu.
- Exception handling chung nên làm giữa sprint chứ không cuối, vì làm sớm sẽ dùng lại được luôn cho Ingredient/Product/Recipe thay vì phải sửa lại 3 chỗ.
