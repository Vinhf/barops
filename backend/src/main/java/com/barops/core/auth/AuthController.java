package com.barops.core.auth;

import com.barops.core.auth.dto.AuthResponse;
import com.barops.core.auth.dto.LoginRequest;
import com.barops.core.auth.dto.RefreshRequest;
import com.barops.core.auth.dto.RegisterRequest;
import com.barops.core.tenant.Branch;
import com.barops.core.tenant.BranchRepository;
import com.barops.core.tenant.Business;
import com.barops.core.tenant.BusinessRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.util.Locale;

/**
 * 3 endpoint DUY NHẤT không cần token (xem JwtAuthenticationFilter.shouldNotFilter
 * và SecurityConfig permitAll cho "/api/auth/**").
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            BusinessRepository businessRepository,
            BranchRepository branchRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.businessRepository = businessRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Tạo Business MỚI + User đầu tiên với role OWNER.
     * Đây là NƠI DUY NHẤT tạo Business qua API công khai — tạo Staff/Manager sau này
     * phải qua UserController (chỉ Owner/Manager đã đăng nhập mới gọi được).
     */
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
        }

        Business business = new Business();
        business.setName(request.businessName());
        business.setSlug(toSlug(request.businessName()));
        business.setTimezone(ZoneId.of("Asia/Ho_Chi_Minh").getId());
        business = businessRepository.save(business);

        // Tạo sẵn 1 Branch mặc định — 1 Business luôn cần ít nhất 1 chi nhánh để vận hành
        // (Table/Order/Ingredient stock sau này đều gắn branch_id, không thể để trống).
        Branch branch = new Branch();
        branch.setName(request.branchName() != null && !request.branchName().isBlank()
                ? request.branchName() : "Chi nhánh chính");
        // Set THỦ CÔNG giống Business — Branch extends BaseTenantEntity nên @PrePersist sẽ
        // cố tự gán businessId từ TenantContext, nhưng lúc này CHƯA đăng nhập nên chưa có
        // TenantContext. Set thủ công trước để @PrePersist thấy đã có giá trị và bỏ qua.
        branch.setBusinessId(business.getId());
        branch = branchRepository.save(branch);

        User owner = new User();
        owner.setEmail(request.email());
        owner.setPasswordHash(passwordEncoder.encode(request.password()));
        owner.setRole(Role.OWNER);
        owner.setActive(true);
        owner.setBusinessId(business.getId());
        // branchId KHÔNG set — giữ null, vì Owner quản lý TẤT CẢ chi nhánh, không riêng branch vừa tạo.
        owner = userRepository.save(owner);

        return issueTokens(owner);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khoá");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu");
        }

        return issueTokens(user);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        Claims claims;
        try {
            claims = jwtService.parseClaims(request.refreshToken());
        } catch (JwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ hoặc đã hết hạn");
        }
        if (!jwtService.isRefreshToken(claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token này không phải refresh token");
        }

        Long userId = jwtService.extractUserId(claims);
        // findById (không qua findByIdForCurrentTenant) vì CHƯA có TenantContext ở bước này —
        // đây là ngoại lệ thứ 2 giống findByEmailIgnoreCase, mục đích chính là để XÁC ĐỊNH lại tenant.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không tồn tại"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khoá");
        }

        // Lấy role/businessId MỚI NHẤT từ DB, không dùng lại claim cũ (đã giải thích trong JwtService).
        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getBusinessId(), user.getBranchId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        return new AuthResponse(accessToken, refreshToken, user.getBusinessId(), user.getBranchId(), user.getRole().name());
    }

    private String toSlug(String name) {
        String base = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        // Thêm hậu tố ngẫu nhiên để tránh trùng slug giữa các quán cùng tên — chưa cần UI đổi slug ở Module 1.
        return base + "-" + Long.toHexString(System.nanoTime() % 0xFFFFF);
    }
}
