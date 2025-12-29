package com.pradeep.ems.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${google.client-id:}")
    private String clientId;

    private final com.pradeep.ems.configuration.JwtAuthenticationConverter jwtAuthenticationConverter;
    private final com.pradeep.ems.configuration.CookieJwtExtractorFilter cookieJwtExtractorFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Enable CORS (uses CorsConfig bean)
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                    // Allow CORS preflight requests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    
                    // Public endpoints
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    
                    // Admin only endpoints (future use)
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    
                    // HR and Admin can manage employees
                    .requestMatchers("/api/v1/employees/**").hasAnyRole("ADMIN", "HR")
                    
                    // All authenticated users can view departments
                    .requestMatchers("/api/v1/departments/**").authenticated()
                    
                    // Reports - Admin and HR only
                    .requestMatchers("/api/v1/reports/**").hasAnyRole("ADMIN", "HR")
                    
                    // All other API endpoints require authentication
                    .requestMatchers("/api/v1/**").authenticated()
                    
                    // All other requests require authentication
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .decoder(jwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter)
                    )
            )
            .addFilterBefore(cookieJwtExtractorFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        
        // Add standard validators
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        
        // Add audience validation if client ID is configured
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(clientId);
        
        // Combine validators
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        
        return jwtDecoder;
    }

    /**
     * Custom validator for audience claim (optional but recommended for security)
     * Validates that the token's audience matches the configured client ID
     */
    private static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;

        public AudienceValidator(String audience) {
            this.audience = audience;
        }

        @Override
        public org.springframework.security.oauth2.core.OAuth2TokenValidatorResult validate(Jwt jwt) {
            // If client ID is not configured, skip audience validation
            if (audience == null || audience.isEmpty() || audience.equals("your-google-client-id-here")) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            
            // Check if the token's audience contains our client ID
            if (jwt.getAudience() != null && jwt.getAudience().contains(audience)) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                new org.springframework.security.oauth2.core.OAuth2Error(
                    "invalid_token",
                    "The token's audience does not match the configured client ID",
                    null
                )
            );
        }
    }
}
