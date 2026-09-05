# BarOps

Hệ thống quản lý vận hành cho 1 quán bar có nhiều chi nhánh (Business — nhiều Branch).
Xem `docs/ERD.md` cho data model, `docs/sprint-1-plan.md` cho kế hoạch Sprint 1.

## Cấu trúc

```
barops/
 ├─ docker-compose.yml       → Postgres local (Redis thêm sau)
 ├─ backend/                  → Spring Boot 4.1.1, Java 25
 │   └─ src/main/java/com/barops/
 │       ├─ core/tenant/      → Business, Branch, BaseTenantEntity, BaseBranchEntity,
 │       │                       TenantContext, TenantAwareRepository
 │       ├─ core/auth/        → User (business_id + branch_id), JwtService,
 │       │                       JwtAuthenticationFilter, CallerBranchContext,
 │       │                       BranchAccessGuard, AuthController, UserController
 │       ├─ core/config/      → SecurityConfig, OpenApiConfig (Swagger)
 │       └─ pos/              → chưa code (Ingredient/Product/Recipe/Order — Sprint 1 Giai đoạn B)
 ├─ frontend/                 → Next.js + TypeScript (skeleton, build UI từ Sprint 4)
 └─ docs/                     → ERD, sprint plan
```

## Chạy lần đầu (Windows, PowerShell)

### 1. Bật database
```powershell
cd barops
docker compose up -d
```
Kiểm tra: `docker ps` — thấy `barops-postgres` là "healthy".

### 2. Chạy backend
Mở `backend/src/main/java/com/barops/BarOpsApplication.java` trong VS Code, bấm nút Run
phía trên hàm `main` (cần Extension Pack for Java + Spring Boot Extension Pack).
Đợi tới khi thấy `Started BarOpsApplication` — backend chạy ở `http://localhost:8080`.

`ddl-auto: update` (trong `application.yml`) khiến Hibernate tự tạo bảng `businesses`,
`branches`, `users` lúc khởi động lần đầu.

### 3. Test API bằng Swagger UI (thay vì Postman)
Mở `http://localhost:8080/swagger-ui/index.html` — danh sách toàn bộ endpoint tự sinh từ code.
1. Mở `POST /api/auth/register` (không khoá — public), "Try it out", điền JSON mẫu, Execute — copy `accessToken` trong response.
2. Bấm **Authorize** (góc trên bên phải), dán `accessToken`, Authorize rồi Close.
3. Từ giờ mọi endpoint có ổ khoá (VD `GET /api/users`) khi Execute đều tự gắn kèm token — không cần tự thêm header `Authorization` như Postman.

### 4. Chạy frontend
```powershell
cd frontend
npm install
npm run dev
```
Frontend chạy ở `http://localhost:3000`.

## Nguyên tắc bắt buộc khi code tiếp (đừng quên)
- Entity thuộc về Business (catalog dùng chung) → `extends BaseTenantEntity`.
- Entity thuộc về 1 Branch cụ thể (Table, Order, BranchMenuItem...) → `extends BaseBranchEntity`,
  và PHẢI gọi `BranchAccessGuard.assertAccess(branchId)` ở đầu controller trước khi set `branchId` thủ công.
- Repository mới → `extends TenantAwareRepository<T>` (tự lọc `business_id`, không tự viết `WHERE` thủ công).

## Tiến độ
- [x] Sprint 0: package structure, `Business`/`Branch` + tenant context, Docker Postgres.
- [x] Sprint 1 — Giai đoạn A: Auth (JWT access+refresh mang `businessId`+`branchId`+`role`),
      `BranchAccessGuard`, Swagger UI.
- [ ] Sprint 1 — Giai đoạn B: CRUD `Product`/`Ingredient`/`Recipe` (catalog) +
      `BranchMenuItem`/`BranchIngredientStock` (theo chi nhánh).
- [ ] ERD vẽ hình chi tiết (`docs/ERD.md` còn ở dạng chữ).
- [ ] GitHub Project board + label (làm trên GitHub, không phải trong code).
