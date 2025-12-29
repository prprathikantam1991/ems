package com.pradeep.ems.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Filter to extract JWT from cookie and add it to Authorization header
 * This allows the Resource Server to validate JWT from cookie
 */
@Component
@Slf4j
public class CookieJwtExtractorFilter extends OncePerRequestFilter {

    @Value("${auth.cookie.name:id_token}")
    private String cookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Check if Authorization header already exists
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Try to extract JWT from cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
                        String token = cookie.getValue();
                        if (token != null && !token.isEmpty()) {
                            log.debug("Extracted JWT from cookie, adding to Authorization header");
                            // Wrap request to add Authorization header
                            request = new HttpServletRequestWrapper(request) {
                                @Override
                                public String getHeader(String name) {
                                    if ("Authorization".equals(name)) {
                                        return "Bearer " + token;
                                    }
                                    return super.getHeader(name);
                                }

                                @Override
                                public Enumeration<String> getHeaders(String name) {
                                    if ("Authorization".equals(name)) {
                                        return Collections.enumeration(Collections.singletonList("Bearer " + token));
                                    }
                                    return super.getHeaders(name);
                                }

                                @Override
                                public Enumeration<String> getHeaderNames() {
                                    java.util.List<String> names = Collections.list(super.getHeaderNames());
                                    if (!names.contains("Authorization")) {
                                        names.add("Authorization");
                                    }
                                    return Collections.enumeration(names);
                                }
                            };
                            break;
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}


