package com.coopcredit.creditapplication.domain.port.out;

import com.coopcredit.creditapplication.domain.model.User;

/**
 * Output Port: JWT Service
 * Interface for JWT token generation and validation.
 */
public interface JwtPort {
    
    String generateToken(User user);
    
    String extractUsername(String token);
    
    boolean validateToken(String token, String username);
    
    boolean isTokenExpired(String token);
}
