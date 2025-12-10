package com.coopcredit.creditapplication.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security Beans Configuration.
 * Provides security-related beans like PasswordEncoder.
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Provides BCrypt password encoder bean.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
