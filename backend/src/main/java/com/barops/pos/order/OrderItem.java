package com.barops.pos.order;

import com.barops.core.tenant.BaseEntity;
import com.barops.pos.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 1 dòng món trong order. Extends BaseEntity (không có business_id riêng — tenant suy ra qua Order).
 *
 * `isComplimentary = true`: món tặng miễn phí — `unitPrice`/`discountAmount` vẫn ghi nhận giá trị
 * gốc để báo cáo chi phí đúng, nhưng khách không phải trả tiền (xử lý ở tổng tiền order, Sprint 3).
 * Vẫn PHẢI trừ kho bình thường theo Recipe dù complimentary — nếu không tồn kho sẽ lệch thực tế.
 */
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "is_complimentary", nullable = false)
    private boolean complimentary = false;
}
