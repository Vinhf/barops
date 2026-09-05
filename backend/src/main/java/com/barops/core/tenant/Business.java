package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Tenant gốc — hiện tại chỉ có 1 bản ghi Business duy nhất (1 quán, nhiều chi nhánh),
 * nhưng vẫn giữ kiến trúc tách Business/Branch để không phải sửa lại nếu sau này mở thêm
 * thương hiệu khác. Business KHÔNG extends BaseTenantEntity (nó không thuộc về tenant nào,
 * chính nó LÀ tenant) — chỉ extends BaseEntity để có id + timestamps.
 *
 * businessHoursConfig KHÔNG còn ở đây — đã chuyển xuống Branch (xem docs/ERD.md,
 * mục "thay đổi định hướng") vì mỗi chi nhánh có giờ mở/đóng khác nhau.
 */
@Getter
@Setter
@Entity
@Table(name = "businesses")
public class Business extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug; // dùng cho URL/onboarding sau này (chưa cần subdomain routing ở MVP)

    private String address;

    @Column(nullable = false)
    private String timezone; // VD "Asia/Ho_Chi_Minh"

    @Embedded
    private ThemeConfig themeConfig = new ThemeConfig();
}
