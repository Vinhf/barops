package com.barops.backend.core.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barops.backend.core.auth.dto.LoginRequest;
import com.barops.backend.core.auth.dto.LoginResponse;
import com.barops.backend.core.auth.dto.RegisterRequest;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.Business;
import com.barops.backend.core.tenant.BusinessRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            BusinessRepository businessRepository,
            BranchRepository branchRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getBusinessId(), user.getBranchId());
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Business business = new Business();
        business.setName(request.getBusinessName());
        Business savedBusiness = businessRepository.save(business);

        Branch branch = new Branch();
        branch.setName(request.getBranchName());
        branch.setBusiness(savedBusiness);
        Branch savedBranch = branchRepository.save(branch);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBusinessId(savedBusiness.getId());
        user.setBranchId(savedBranch.getId());

        Role role = Role.valueOf(request.getRole().toUpperCase());
        user.setRole(role);

        return userRepository.save(user);
    }
}