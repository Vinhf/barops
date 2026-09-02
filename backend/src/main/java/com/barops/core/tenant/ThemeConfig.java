package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Cấu hình giao diện riêng của từng quán (tenant) — dùng để Next.js
 * áp CSS variable + logo động, không code UI riêng cho từng quán.
 */
@Getter
@Setter
@Embeddable
public class ThemeConfig {

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "secondary_color")
    private String secondaryColor;

    @Column(name = "display_name")
    private String displayName;
}
