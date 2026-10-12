package com.barops.pos.product;

import com.barops.pos.product.dto.BranchMenuItemRequest;
import com.barops.pos.product.dto.BranchMenuItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/{branchId}/menu-items")
public class BranchMenuController {

    private final BranchMenuService branchMenuService;

    public BranchMenuController(BranchMenuService branchMenuService) {
        this.branchMenuService = branchMenuService;
    }

    @GetMapping
    public List<BranchMenuItemResponse> list(@PathVariable Long branchId) {
        return branchMenuService.list(branchId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public BranchMenuItemResponse create(
            @PathVariable Long branchId,
            @Valid @RequestBody BranchMenuItemRequest request
    ) {
        return branchMenuService.create(branchId, request);
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public BranchMenuItemResponse update(
            @PathVariable Long branchId,
            @PathVariable Long itemId,
            @Valid @RequestBody BranchMenuItemRequest request
    ) {
        return branchMenuService.update(branchId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public void delete(@PathVariable Long branchId, @PathVariable Long itemId) {
        branchMenuService.delete(branchId, itemId);
    }
}
