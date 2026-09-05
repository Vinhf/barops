package com.barops.pos.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /** Sprint 2 dùng để lấy toàn bộ nguyên liệu cần trừ kho khi 1 Product được order. */
    List<Recipe> findByProductId(Long productId);
}
