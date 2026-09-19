package com.barops.backend.core.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barops.backend.core.tenant.Branch;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByBranch(Branch branch);
}