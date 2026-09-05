package com.barops.core.auth.dto;

import com.barops.core.auth.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự") String password,
        @NotNull Role role,
        // Bắt buộc nếu role là MANAGER/STAFF (gắn cố định 1 chi nhánh).
        // PHẢI null nếu role là OWNER (Owner không gắn chi nhánh nào — quản lý tất cả).
        // Validate 2 quy tắc này ở UserController, không dùng annotation vì phụ thuộc vào role.
        Long branchId
) {
}
