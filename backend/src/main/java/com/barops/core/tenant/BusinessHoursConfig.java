package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Giờ mở/đóng cửa của quán. Dùng để tính "business_date" đúng cho các quán
 * hoạt động qua nửa đêm (VD 18:00 -> 01:00 hôm sau vẫn tính là cùng 1 "ngày kinh doanh").
 *
 * Logic quy đổi order.createdAt -> business_date sẽ nằm ở service layer của module `pos`
 * (Sprint 2-3), class này chỉ lưu cấu hình.
 */
@Getter
@Setter
@Embeddable
public class BusinessHoursConfig {

    @Column(name = "opening_time")
    private LocalTime openingTime; // VD 18:00

    @Column(name = "closing_time")
    private LocalTime closingTime; // VD 01:00 (qua nửa đêm nếu closingTime < openingTime)
}
