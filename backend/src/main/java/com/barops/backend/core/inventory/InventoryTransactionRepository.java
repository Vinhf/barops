package com.barops.backend.core.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.tenant.Branch;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    List<InventoryTransaction> findByBranchOrderByCreatedAtDesc(Branch branch);
}
