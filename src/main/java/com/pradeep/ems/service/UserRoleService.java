package com.pradeep.ems.service;

import com.pradeep.ems.entity.Role;
import com.pradeep.ems.entity.User;
import com.pradeep.ems.repository.RoleRepository;
import com.pradeep.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Get user roles from database based on Google email
     * If user doesn't exist, creates a new user with default USER role
     */
    @Transactional(readOnly = true)
    public Collection<GrantedAuthority> getAuthorities(String email, String googleId, String name, String picture) {
        // Try to find user by email first, then by Google ID
        User user = userRepository.findByEmailWithRoles(email)
                .orElseGet(() -> userRepository.findByGoogleIdWithRoles(googleId).orElse(null));

        if (user == null) {
            // User doesn't exist - create new user with default USER role
            log.info("Creating new user: {}", email);
            user = createUserWithDefaultRole(email, googleId, name, picture);
        } else {
            // Update user info if needed (name, picture might have changed)
            boolean updated = false;
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
                updated = true;
            }
            if (picture != null && !picture.equals(user.getPicture())) {
                user.setPicture(picture);
                updated = true;
            }
            if (updated) {
                userRepository.save(user);
            }
        }

        // Convert roles to Spring Security authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
        });
        return authorities;
    }

    /**
     * Create a new user with default USER role
     */
    @Transactional
    private User createUserWithDefaultRole(String email, String googleId, String name, String picture) {
        Role defaultRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default USER role not found in database"));

        User newUser = User.builder()
                .email(email)
                .googleId(googleId)
                .name(name)
                .picture(picture)
                .roles(Set.of(defaultRole))
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Get authorities for existing user (no user creation)
     */
    @Transactional(readOnly = true)
    public Collection<GrantedAuthority> getAuthoritiesForExistingUser(String email) {
        return userRepository.findByEmailWithRoles(email)
                .map(user -> {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    user.getRoles().forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
                    });
                    return authorities;
                })
                .orElse(Collections.emptyList());
    }
}

