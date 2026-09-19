package com.barops.backend.core.tenant;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
}
