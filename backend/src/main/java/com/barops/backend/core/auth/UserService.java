package com.barops.backend.core.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BranchRepository branchRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.from(user);
    }

    public List<UserResponse> getUsersForCurrentBranch() {
        UUID businessId = TenantContext.getCurrentBusinessId();
        UUID branchId = TenantContext.getCurrentBranchId();

        if (businessId == null || branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        return userRepository.findByBusinessIdAndBranchId(businessId, branchId)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse createUser(UserCreateRequest request) {
        UUID businessId = TenantContext.getCurrentBusinessId();
        UUID branchId = TenantContext.getCurrentBranchId();

        if (businessId == null || branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        if (!branch.getBusiness().getId().equals(businessId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch does not belong to this business");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setBusinessId(businessId);
        user.setBranchId(branchId);

        return UserResponse.from(userRepository.save(user));
    }
}
