# BarOps

CRM + ERP cho bar/pub, multi-tenant SaaS. Xem chi tiết trong `docs/` (bổ sung dần theo doc setup gốc).

## Cấu trúc

```
barops/
 ├─ docker-compose.yml       → Postgres local (Redis thêm sau)
 ├─ backend/                  → Spring Boot 3 (Java 17)
 │   (Java 25, Spring Boot 4.1.1)
 │   └─ src/main/java/com/barops/
 │       ├─ core/tenant/      → Business, BaseTenantEntity, TenantContext, TenantAwareRepository
 │       ├─ core/auth/        → User, Role (JWT/login làm Sprint 1)
 │       └─ pos/              → Module 1 (Sprint 1-3)
 ├─ frontend/                 → Next.js + TypeScript (skeleton, build UI từ Sprint 4)
 └─ docs/                     → ERD, kiến trúc, ghi chú sprint
```

## Chạy lần đầu (Windows, PowerShell)

### 1. Bật database
```powershell
cd barops
docker compose up -d
```
Kiểm tra container chạy: `docker ps` — thấy `barops-postgres` là "healthy".

### 2. Chạy backend
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
> Lưu ý: repo này chưa kèm sẵn Maven Wrapper (`mvnw.cmd`). Cách nhanh nhất để có nó:
> mở project bằng VS Code (đã cài Extension Pack for Java + Spring Boot Extension Pack) →
> VS Code sẽ tự nhận `pom.xml` và cho bạn chạy `BarOpsApplication.java` trực tiếp bằng nút Run
> phía trên hàm `main`, không cần mvnw. Hoặc nếu có Maven cài sẵn: `mvn spring-boot:run`.

Backend chạy ở `http://localhost:8080`. Vì `ddl-auto: update` (xem `application.yml`),
Hibernate sẽ tự tạo bảng `businesses`, `users` trong Postgres lúc khởi động lần đầu — vào
kiểm tra bằng extension "PostgreSQL" trong VS Code, connect với:
- host: `localhost`, port: `5432`, database: `barops`, user: `barops`, password: `barops_local_pw`

### 3. Chạy frontend
```powershell
cd frontend
npm install
npm run dev
```
Frontend chạy ở `http://localhost:3000`.

## Nguyên tắc bắt buộc khi code tiếp (đừng quên)
- Mọi entity nghiệp vụ mới → `extends BaseTenantEntity` (không tự thêm field `businessId` riêng).
- Mọi repository mới → `extends TenantAwareRepository<T>` (không tự viết `WHERE business_id = ?`).
- Đọc `docs/ERD.md` và hoàn thiện ERD chi tiết TRƯỚC khi bắt đầu Sprint 1.

## Sprint hiện tại: Sprint 0
Việc còn lại trong Sprint 0 (xem mục 7 trong doc gốc):
- [x] Setup package structure (modular)
- [x] `Business` entity + cơ chế xác định tenant (TenantContext, base repository)
- [x] Docker Compose Postgres local
- [ ] ERD chi tiết (xem `docs/ERD.md`, còn dở)
- [ ] Setup GitHub Project board + label (làm trên GitHub, không phải trong code)
