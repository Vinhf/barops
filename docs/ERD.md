# ERD — Module 1 (đã đổi hướng: 1 quán, nhiều chi nhánh — xem lịch sử trao đổi ngày cập nhật gần nhất)

> **Thay đổi định hướng quan trọng**: không làm SaaS nhiều quán khác nhau nữa. Tập trung
> 1 Business (quán) có nhiều Branch (chi nhánh). Nguyên tắc tách bảng: **Business sở hữu
> catalog dùng chung** (định nghĩa 1 lần), **Branch sở hữu dữ liệu vận hành** (mỗi nơi 1 bản ghi riêng).

## Catalog (Business — định nghĩa chung, không lặp lại theo từng chi nhánh)
- `Product`: id, business_id, name, display_name_en, is_active — CHỈ là định nghĩa món, KHÔNG có giá cuối/tồn kho ở đây.
- `Ingredient`: id, business_id, name, unit — CHỈ là định nghĩa nguyên liệu (VD "Vodka Absolut", đơn vị ml), KHÔNG có số lượng tồn ở đây.
- `Recipe` (BOM): id, business_id, product_id, ingredient_id, quantity — công thức dùng chung cho mọi chi nhánh bán món đó.

## Vận hành (Branch — mỗi chi nhánh 1 bản ghi riêng, tham chiếu tới catalog)
- `Branch`: id, business_id, name, address, business_hours_config (giờ mở/đóng — CHUYỂN từ Business xuống đây vì mỗi chi nhánh giờ khác nhau).
- `BranchMenuItem`: id, business_id, branch_id, product_id, price, is_available — đây là "menu thật" khách nhìn thấy. 1 Product có thể xuất hiện ở nhiều Branch với giá khác nhau, hoặc chỉ xuất hiện ở 1 Branch (món riêng) bằng cách không tạo BranchMenuItem ở branch kia.
- `BranchIngredientStock`: id, business_id, branch_id, ingredient_id, stock_quantity — tồn kho THẬT, riêng từng chi nhánh. 1 Ingredient (catalog) có N dòng BranchIngredientStock (1 dòng/chi nhánh).
- `StockTransfer`: id, business_id, ingredient_id, source_branch_id, destination_branch_id, quantity, status, created_at — ghi nhận việc chuyển nguyên liệu giữa 2 chi nhánh; xử lý thật (trừ/cộng `BranchIngredientStock` trong 1 transaction) làm ở Sprint 2.
- `Table`, `Order`, `OrderItem`: gắn `branch_id` (không chỉ `business_id`) — mỗi chi nhánh có bộ bàn/đơn hàng riêng hoàn toàn.
- `PromotionRule`: id, business_id, branch_id, event_name (nullable, VD "Countdown 2027"), valid_from, valid_to, day_of_week, time_range, discount_type, discount_value — thêm `valid_from`/`valid_to` so với thiết kế cũ để hỗ trợ "theo mùa/sự kiện", và bắt buộc `branch_id` vì mỗi chi nhánh tự quyết khuyến mãi riêng.

## User — gắn cả Business lẫn Branch
- `User`: id, business_id, branch_id (**nullable**), email, password_hash, role.
- `branch_id = null` → Owner, xem/quản lý được TẤT CẢ chi nhánh của Business.
- `branch_id` có giá trị → Manager/Staff, chỉ thao tác được đúng chi nhánh đó. Điều chuyển = update lại giá trị này, chưa cần lưu lịch sử.

## Vì sao tách Business (catalog) / Branch (vận hành) thay vì nhét hết vào Branch
Nếu để `Ingredient` có `stock_quantity` ngay trên nó (thiết kế cũ) thì 1 chi nhánh = 1 dòng `Ingredient` riêng biệt, không có cách nào biết "Vodka Absolut" ở chi nhánh A và chi nhánh B là CÙNG 1 loại nguyên liệu để `StockTransfer` giữa 2 bên — phải tự dò theo tên (dễ sai chính tả, dễ trùng tên khác nghĩa). Tách riêng: `Ingredient` (catalog, định nghĩa 1 lần) + `BranchIngredientStock` (số lượng riêng từng nơi, cùng trỏ về 1 `ingredient_id`) giải quyết gọn vấn đề này — `StockTransfer` chỉ cần biết `ingredient_id` + 2 `branch_id`, không cần đoán.

---

# ERD gốc (Module 1, bản trước khi đổi hướng — giữ lại tham khảo)

> Việc quan trọng nhất Sprint 0: đừng vội, sai ERD ở đây thì Sprint 3-4 phải sửa lại rất tốn thời gian.

## Bảng & quan hệ chính (dựa theo mục 6 trong project setup doc)

```
Business (1) ───< User (N)
Business (1) ───< Ingredient (N)
Business (1) ───< Product (N)
Business (1) ───< Table (N)
Business (1) ───< Order (N)
Business (1) ───< PromotionRule (N)

Product (1) ───< Recipe (N) >─── (1) Ingredient      // BOM: 1 product nhiều ingredient + định lượng
Order (1) ───< OrderItem (N) >─── (1) Product
Table (1) ───< Order (N)
```

## Việc cần làm ở Sprint 0
- [ ] Vẽ ERD chi tiết (dùng dbdiagram.io hoặc draw.io) — export ảnh lưu vào `docs/erd.png`
- [ ] Chốt kiểu dữ liệu từng cột (đặc biệt: `price`/`unit_price`/`discount_amount` dùng `NUMERIC(12,2)`,
      không dùng `FLOAT`/`DOUBLE` để tránh sai số tiền tệ)
- [ ] Chốt index cần thiết: `(business_id)` trên mọi bảng tenant, `(business_id, business_date)` trên `orders`
      (query dashboard "doanh thu hôm nay" sẽ chạy rất thường xuyên)
- [ ] Chốt cascade rule: xoá `Product` thì `Recipe` liên quan xử lý sao (soft delete `is_active` thay vì xoá cứng —
      đã có sẵn field `is_active` trên Product theo doc)
