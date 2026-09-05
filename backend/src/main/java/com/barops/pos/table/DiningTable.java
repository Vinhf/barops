package com.barops.pos.table;

import com.barops.core.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Bàn trong quán. Đặt tên class là `DiningTable` thay vì `Table` (dù data model trong doc
 * gọi là "Table") để tránh trùng tên với annotation `jakarta.persistence.Table` trong cùng file
 * — tên bảng SQL vẫn là `tables` như bình thường.
 */
@Getter
@Setter
@Entity
@Table(name = "tables")
public class DiningTable extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;
}
