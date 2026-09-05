package com.barops.core.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Dùng cho mọi entity thuộc về ĐÚNG 1 chi nhánh (Table, Order, BranchMenuItem,
 * BranchIngredientStock, PromotionRule...) — khác với BaseTenantEntity (chỉ có business_id).
 *
 * QUAN TRỌNG — khác với business_id: branch_id KHÔNG tự động gán từ context (không có
 * @PrePersist như BaseTenantEntity). Lý do: Owner không cố định 1 chi nhánh (branch_id
 * trong JWT của Owner là null vì họ quản lý TẤT CẢ chi nhánh), nên không thể suy ra
 * "chi nhánh nào" một cách tự động như suy ra "business nào" được.
 *
 * Thay vào đó: branchId PHẢI được set THỦ CÔNG ở tầng Controller/Service, sau khi đã
 * gọi BranchAccessGuard (Sprint 1, package core/auth) để xác nhận người gọi có quyền
 * thao tác trên đúng branchId đó (Staff/Manager chỉ được branch của mình, Owner được cả).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseBranchEntity extends BaseTenantEntity {

    @Column(name = "branch_id", nullable = false, updatable = false)
    private Long branchId;
}
