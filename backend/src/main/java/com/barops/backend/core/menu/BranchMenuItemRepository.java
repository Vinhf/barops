package com.barops.backend.core.menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.tenant.Branch;

public interface BranchMenuItemRepository extends JpaRepository<BranchMenuItem, UUID> {
    List<BranchMenuItem> findByBranchAndActiveTrue(Branch branch);
    Optional<BranchMenuItem> findByIdAndBranch(UUID id, Branch branch);
}
