package com.barops.pos.promotion;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Quy tắc giảm giá tự động theo ngày/khung giờ (kiểu happy hour) — pattern chung ngành F&B,
 * mỗi tenant tự cấu hình rule riêng của quán mình, không hardcode tên chương trình cụ thể.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_rules")
public class PromotionRule extends BaseTenantEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "time_range_start", nullable = false)
    private LocalTime timeRangeStart;

    @Column(name = "time_range_end", nullable = false)
    private LocalTime timeRangeEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    /** PERCENTAGE: 0-100. FIXED_AMOUNT: số tiền trừ thẳng (cùng đơn vị tiền tệ với Product.price). */
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
