package com.pradeep.ems.configuration;

import com.pradeep.ems.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Custom JWT authentication converter that adds database roles to JWT authorities
 * Fetches roles from User Service via UserServiceClient
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserServiceClient userServiceClient;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract user information from JWT
        String email = jwt.getClaimAsString("email");
        String googleId = jwt.getSubject(); // "sub" claim

        // Get authorities from User Service via UserServiceClient
        Collection<GrantedAuthority> authorities = getAuthoritiesFromUserService(email, googleId);

        // Create JWT authentication token with roles
        return new JwtAuthenticationToken(jwt, authorities);
    }

    /**
     * Get authorities from User Service, trying email first, then Google ID as fallback
     */
    private Collection<GrantedAuthority> getAuthoritiesFromUserService(String email, String googleId) {
        // Try to get authorities by email first
        if (email != null && !email.isEmpty()) {
            Collection<GrantedAuthority> authorities = userServiceClient.getUserAuthorities(email);
            if (!authorities.isEmpty()) {
                return authorities;
            }
        }

        // Fallback: try by Google ID
        if (googleId != null && !googleId.isEmpty()) {
            Collection<GrantedAuthority> authorities = userServiceClient.getUserAuthoritiesByGoogleId(googleId);
            if (!authorities.isEmpty()) {
                return authorities;
            }
        }

        log.warn("No authorities found for user: email={}, googleId={}", email, googleId);
        return java.util.Collections.emptyList();
    }
}

