package com.pradeep.ems.configuration;

import com.pradeep.ems.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Custom JWT authentication converter that adds database roles to JWT authorities
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRoleService userRoleService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract user information from JWT
        String email = jwt.getClaimAsString("email");
        String googleId = jwt.getSubject(); // "sub" claim
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        // Get roles from database and convert to authorities
        Collection<GrantedAuthority> authorities = userRoleService.getAuthorities(
                email, googleId, name, picture
        );

        // Create JWT authentication token with roles
        return new JwtAuthenticationToken(jwt, authorities);
    }
}

