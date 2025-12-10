package com.coopcredit.creditapplication.domain.port.out;

/**
 * Output Port: Password Encoder Service
 * Interface for password encryption and validation.
 */
public interface PasswordEncoderPort {
    
    String encode(String rawPassword);
    
    boolean matches(String rawPassword, String encodedPassword);
}
