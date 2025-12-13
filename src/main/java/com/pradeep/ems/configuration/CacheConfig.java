package com.pradeep.ems.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration
 * 
 * Configures Caffeine cache for application-level caching.
 * Demonstrates Spring Cache abstraction with Caffeine as the cache provider.
 * 
 * Note: @EnableCaching is already enabled in EmployeeManagementApplication
 */
@Configuration
public class CacheConfig {

    /**
     * Configure Caffeine Cache Manager
     * 
     * Creates cache manager with:
     * - Maximum 1000 entries per cache
     * - 10 minute expiration after write
     * - 5 minute expiration after access
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure cache builder
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)                    // Maximum 1000 entries
            .expireAfterWrite(10, TimeUnit.MINUTES)  // Expire 10 minutes after write
            .expireAfterAccess(5, TimeUnit.MINUTES)   // Expire 5 minutes after last access
            .recordStats());                      // Enable cache statistics
        
        // Define cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "departments",      // Cache for departments
            "employees",        // Cache for employees
            "departmentEmployees"  // Cache for department-employee relationships
        ));
        
        return cacheManager;
    }
}

