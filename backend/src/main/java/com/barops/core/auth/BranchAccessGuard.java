package com.barops.core.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Gọi ở ĐẦU mỗi controller method thao tác trên 1 branch cụ thể (branchId lấy từ URL,
 * ví dụ /api/branches/{branchId}/ingredients). Đây là nơi DUY NHẤT chứa luật:
 *
 *   - Owner (CallerBranchContext = null) => luôn được phép, bất kể branchId nào.
 *   - Manager/Staff (CallerBranchContext có giá trị) => CHỈ được phép nếu branchId trong URL
 *     TRÙNG với branch của chính họ. Khác thì chặn 403 — kể cả khi họ có JWT hợp lệ,
 *     vì hợp lệ về đăng nhập KHÔNG có nghĩa là hợp lệ về phạm vi chi nhánh.
 *
 * Không dùng @PreAuthorize cho việc này vì @PreAuthorize chỉ so sánh được role tĩnh
 * (OWNER/MANAGER/STAFF), không so sánh được 2 giá trị động (branchId trong JWT vs branchId
 * trong URL) — nên phải viết thành 1 method gọi tay trong controller.
 */
@Component
public class BranchAccessGuard {

    public void assertAccess(Long requestedBranchId) {
        Long callerBranchId = CallerBranchContext.get();

        // callerBranchId == null => Owner, không giới hạn.
        if (callerBranchId == null) {
            return;
        }

        if (!callerBranchId.equals(requestedBranchId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Bạn không có quyền thao tác trên chi nhánh này"
            );
        }
    }
}
