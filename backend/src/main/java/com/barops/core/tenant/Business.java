package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Tenant gốc. Business KHÔNG extends BaseTenantEntity (nó không thuộc về tenant nào,
 * chính nó LÀ tenant) — chỉ extends BaseEntity để có id + timestamps.
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

    @Embedded
    private BusinessHoursConfig businessHoursConfig = new BusinessHoursConfig();
}
