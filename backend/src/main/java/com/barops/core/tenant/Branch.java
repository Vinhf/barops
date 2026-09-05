package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Chi nhánh — extends BaseTenantEntity (có business_id, TỰ ĐỘNG gán được vì lúc tạo Branch
 * luôn là Owner đang đăng nhập, JWT của Owner có business_id rõ ràng — khác với branch_id
 * ở BaseBranchEntity không tự gán được).
 *
 * businessHoursConfig CHUYỂN từ Business xuống đây — vì mỗi chi nhánh giờ mở/đóng khác nhau
 * (yêu cầu thực tế: "sẽ có quán đóng sớm đóng trễ").
 */
@Getter
@Setter
@Entity
@Table(name = "branches")
public class Branch extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    private String address;

    @Embedded
    private BusinessHoursConfig businessHoursConfig = new BusinessHoursConfig();
}
