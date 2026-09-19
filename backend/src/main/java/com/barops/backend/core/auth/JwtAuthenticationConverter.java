package com.barops.backend.core.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getSubject();

        Collection<String> authoritiesClaim = jwt.getClaimAsStringList("authorities");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (authoritiesClaim != null) {
            for (String authority : authoritiesClaim) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}