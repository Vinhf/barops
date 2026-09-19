package com.barops.backend.core.auth.dto;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private UUID businessId;
    private UUID branchId;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String role, UUID businessId, UUID branchId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.businessId = businessId;
        this.branchId = branchId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getBusinessId() {
        return businessId;
    }

    public void setBusinessId(UUID businessId) {
        this.businessId = businessId;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }
}