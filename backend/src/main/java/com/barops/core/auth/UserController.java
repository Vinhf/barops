package com.barops.core.auth;

import com.barops.core.auth.dto.CreateUserRequest;
import com.barops.core.auth.dto.UserResponse;
import com.barops.core.tenant.BranchRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Ví dụ cụ thể cho việc phân quyền (RBAC) đã giải thích: chỉ Owner/Manager được tạo tài khoản mới,
 * Staff bị chặn ở tầng framework (@PreAuthorize), không cần if/else thủ công trong method.
 *
 * Tất cả method ở đây chạy SAU khi JwtAuthenticationFilter đã set xong TenantContext,
 * nên userRepository.findAllForCurrentTenant() tự động chỉ trả về user của ĐÚNG business đang đăng nhập.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final BranchAccessGuard branchAccessGuard;

    public UserController(
            UserRepository userRepository,
            BranchRepository branchRepository,
            PasswordEncoder passwordEncoder,
            BranchAccessGuard branchAccessGuard
    ) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
        this.branchAccessGuard = branchAccessGuard;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userRepository.findAllForCurrentTenant().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * hasAnyRole('OWNER','MANAGER') -> Staff gọi endpoint này sẽ nhận 403 Forbidden
     * NGAY TỪ TẦNG SECURITY, method bên dưới không hề chạy.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
        }
        // Chỉ Owner mới được tạo thêm Owner khác — Manager không được nâng quyền ngang mình.
        // Đây là ví dụ permission chi tiết HƠN mức @PreAuthorize có thể check bằng annotation đơn thuần,
        // nên phải viết thêm điều kiện thủ công ở đây.
        boolean callerIsOwner = hasRole("OWNER");
        if (request.role() == Role.OWNER && !callerIsOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ Owner mới được tạo tài khoản Owner khác");
        }

        // Quy tắc branchId theo role — xem giải thích trong CreateUserRequest.
        if (request.role() == Role.OWNER) {
            if (request.branchId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner không được gắn branchId");
            }
        } else {
            if (request.branchId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager/Staff bắt buộc phải có branchId");
            }
            // Chặn Manager tạo Staff cho MỘT CHI NHÁNH KHÁC chi nhánh của chính họ.
            // Owner gọi hàm này thì luôn qua (CallerBranchContext null => assertAccess không chặn gì).
            branchAccessGuard.assertAccess(request.branchId());
            // Xác nhận branch đó thật sự tồn tại VÀ thuộc đúng business hiện tại
            // (findByIdForCurrentTenant tự lọc theo TenantContext — không tin branchId client gửi lên mù quáng).
            branchRepository.findByIdForCurrentTenant(request.branchId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi nhánh không tồn tại"));
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setBranchId(request.branchId());
        user.setActive(true);
        // KHÔNG set businessId thủ công ở đây — để BaseTenantEntity tự gán từ TenantContext,
        // đúng luồng bình thường (khác với AuthController.register là ngoại lệ).
        user = userRepository.save(user);

        return UserResponse.from(user);
    }

    private boolean hasRole(String role) {
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
