package com.infracore.config;

import com.infracore.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializationConfig {

    private final AuthService authService;

    // Commented out since we're using Liquibase for initial data
    // @Bean
    public CommandLineRunner initData() {
        return args -> {
            try {
                log.info("Initializing default roles...");
                authService.initializeRoles();
                log.info("Default roles initialized successfully");
                
                log.info("Creating admin user...");
                authService.createAdminUser();
                log.info("Admin user created successfully");
                
            } catch (Exception e) {
                log.error("Error during data initialization", e);
            }
        };
    }
} 