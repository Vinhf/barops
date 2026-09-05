package com.barops.core.auth.dto;

import com.barops.core.auth.Role;
import com.barops.core.auth.User;

public record UserResponse(Long id, String email, Role role, Long branchId, boolean active) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getBranchId(), user.isActive());
    }
}
