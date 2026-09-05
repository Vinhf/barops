package com.barops.core.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Tên quán không được để trống")
        String businessName,

        // Tuỳ chọn — vì 1 Business luôn cần ÍT NHẤT 1 Branch để vận hành, nhưng không bắt
        // Owner phải nghĩ tên chi nhánh ngay lúc đăng ký. Không nhập thì dùng tên mặc định.
        String branchName,

        @NotBlank @Email(message = "Email không hợp lệ")
        String email,

        @NotBlank
        @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự")
        String password
) {
}
