package com.coopcredit.creditapplication.infrastructure.adapter.out.security;

import com.coopcredit.creditapplication.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password Encoder Adapter implementing PasswordEncoderPort.
 * Delegates to Spring Security's BCryptPasswordEncoder.
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Encodes a raw password using BCrypt.
     *
     * @param rawPassword the plain text password
     * @return encrypted password
     */
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Checks if raw password matches encoded password.
     *
     * @param rawPassword the plain text password
     * @param encodedPassword the encrypted password
     * @return true if passwords match
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
