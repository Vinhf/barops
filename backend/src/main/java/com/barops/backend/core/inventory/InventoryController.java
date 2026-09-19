package com.barops.backend.core.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public List<BranchIngredientStock> findAll() {
        return inventoryService.findAll();
    }

    @PostMapping("/ingredients/{ingredientId}/adjust")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    public BranchIngredientStock adjust(@PathVariable UUID ingredientId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return inventoryService.adjust(ingredientId, request);
    }
}
