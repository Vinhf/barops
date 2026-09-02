# ERD — Module 1 (nháp, cần vẽ hình chi tiết trước khi code Sprint 1)

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
