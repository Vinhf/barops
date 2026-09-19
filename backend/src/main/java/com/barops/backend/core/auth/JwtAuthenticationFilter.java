package com.barops.backend.core.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.barops.backend.core.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Jwt jwt = jwtDecoder.decode(token);

                String username = jwt.getSubject();
                String businessId = jwt.getClaimAsString("businessId");
                String branchId = jwt.getClaimAsString("branchId");

                List<GrantedAuthority> authorities = new ArrayList<>();
                List<String> claims = jwt.getClaimAsStringList("authorities");

                if (claims != null) {
                    for (String authority : claims) {
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (businessId != null && !businessId.isBlank()) {
                    TenantContext.setCurrentBusinessId(UUID.fromString(businessId));
                }

                if (branchId != null && !branchId.isBlank()) {
                    TenantContext.setCurrentBranchId(UUID.fromString(branchId));
                }
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                TenantContext.clearBusinessId();
                TenantContext.clearBranchId();
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            TenantContext.clearBusinessId();
            TenantContext.clearBranchId();
        }
    }
}
