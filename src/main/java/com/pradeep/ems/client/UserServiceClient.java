package com.pradeep.ems.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client service for interacting with User Service REST API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    /**
     * Get user authorities by email
     * @param email User email
     * @return Collection of GrantedAuthority, empty if user not found or error occurs
     */
    public Collection<GrantedAuthority> getUserAuthorities(String email) {
        if (email == null || email.isEmpty()) {
            log.warn("Email is null or empty, cannot fetch authorities");
            return Collections.emptyList();
        }

        try {
            String url = userServiceUrl + "/api/users/{email}/authorities";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    email
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("authorities")) {
                    @SuppressWarnings("unchecked")
                    List<String> authorityStrings = (List<String>) body.get("authorities");
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorityStrings.forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth)));
                    log.debug("Retrieved {} authorities for user: {}", authorities.size(), email);
                    return authorities;
                }
            }
        } catch (RestClientException e) {
            log.warn("Failed to get authorities by email for user: {}", email, e);
        } catch (Exception e) {
            log.error("Unexpected error getting authorities by email for user: {}", email, e);
        }

        return Collections.emptyList();
    }

    /**
     * Get user authorities by Google ID
     * @param googleId User Google ID
     * @return Collection of GrantedAuthority, empty if user not found or error occurs
     */
    public Collection<GrantedAuthority> getUserAuthoritiesByGoogleId(String googleId) {
        if (googleId == null || googleId.isEmpty()) {
            log.warn("Google ID is null or empty, cannot fetch authorities");
            return Collections.emptyList();
        }

        try {
            String url = userServiceUrl + "/api/users/google/{googleId}/authorities";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    googleId
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("authorities")) {
                    @SuppressWarnings("unchecked")
                    List<String> authorityStrings = (List<String>) body.get("authorities");
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorityStrings.forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth)));
                    log.debug("Retrieved {} authorities for user by Google ID: {}", authorities.size(), googleId);
                    return authorities;
                }
            }
        } catch (RestClientException e) {
            log.warn("Failed to get authorities by Google ID for user: {}", googleId, e);
        } catch (Exception e) {
            log.error("Unexpected error getting authorities by Google ID for user: {}", googleId, e);
        }

        return Collections.emptyList();
    }

    /**
     * Get user by email (for future use)
     * @param email User email
     * @return User data as Map, null if not found or error occurs
     */
    public Map<String, Object> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            log.warn("Email is null or empty, cannot fetch user");
            return null;
        }

        try {
            String url = userServiceUrl + "/api/users/{email}";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    email
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (RestClientException e) {
            log.warn("Failed to get user by email: {}", email, e);
        } catch (Exception e) {
            log.error("Unexpected error getting user by email: {}", email, e);
        }

        return null;
    }

    /**
     * Get user by Google ID (for future use)
     * @param googleId User Google ID
     * @return User data as Map, null if not found or error occurs
     */
    public Map<String, Object> getUserByGoogleId(String googleId) {
        if (googleId == null || googleId.isEmpty()) {
            log.warn("Google ID is null or empty, cannot fetch user");
            return null;
        }

        try {
            String url = userServiceUrl + "/api/users/google/{googleId}";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    googleId
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (RestClientException e) {
            log.warn("Failed to get user by Google ID: {}", googleId, e);
        } catch (Exception e) {
            log.error("Unexpected error getting user by Google ID: {}", googleId, e);
        }

        return null;
    }
}


