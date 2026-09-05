package com.barops.pos.order;

import com.barops.core.tenant.BaseTenantEntity;
import com.barops.pos.table.DiningTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * `businessDate` KHÔNG phải ngày dương lịch của createdAt — phải tính theo
 * `Business.businessHoursConfig` (nếu giờ mở cửa qua nửa đêm, vd 18:00–01:00, thì order lúc
 * 00:30 vẫn thuộc "ngày kinh doanh" hôm trước). Logic quy đổi sẽ viết ở OrderService (Sprint 2),
 * ở đây chỉ khai báo cột — bắt buộc index chung với business_id vì dashboard sẽ query theo
 * cặp (business_id, business_date) rất thường xuyên.
 */
@Getter
@Setter
@Entity
@Table(
    name = "orders",
    indexes = @Index(name = "idx_orders_business_date", columnList = "business_id, business_date")
)
public class Order extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable table;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.OPEN;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;
}
