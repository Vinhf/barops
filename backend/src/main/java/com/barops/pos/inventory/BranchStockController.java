package com.barops.pos.inventory;

import com.barops.pos.inventory.dto.BranchStockRequest;
import com.barops.pos.inventory.dto.BranchStockResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/{branchId}/inventory/stocks")
public class BranchStockController {

    private final BranchStockService branchStockService;

    public BranchStockController(BranchStockService branchStockService) {
        this.branchStockService = branchStockService;
    }

    @GetMapping
    public List<BranchStockResponse> list(@PathVariable Long branchId) {
        return branchStockService.list(branchId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public BranchStockResponse create(
            @PathVariable Long branchId,
            @Valid @RequestBody BranchStockRequest request
    ) {
        return branchStockService.create(branchId, request);
    }

    @PutMapping("/{stockId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public BranchStockResponse update(
            @PathVariable Long branchId,
            @PathVariable Long stockId,
            @Valid @RequestBody BranchStockRequest request
    ) {
        return branchStockService.update(branchId, stockId, request);
    }

    @DeleteMapping("/{stockId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public void delete(@PathVariable Long branchId, @PathVariable Long stockId) {
        branchStockService.delete(branchId, stockId);
    }
}
