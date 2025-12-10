package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AuthResponse;
import com.coopcredit.creditapplication.application.dto.LoginRequest;
import com.coopcredit.creditapplication.domain.model.User;
import com.coopcredit.creditapplication.domain.port.out.JwtPort;
import com.coopcredit.creditapplication.domain.port.out.PasswordEncoderPort;
import com.coopcredit.creditapplication.domain.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Use Case: Authenticate user and generate JWT token.
 */
@Service
@Transactional(readOnly = true)
public class AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthenticateUserUseCase(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
    }

    /**
     * Executes user authentication.
     *
     * @param request login credentials
     * @return authentication response with JWT token
     * @throws IllegalArgumentException if authentication fails
     */
    public AuthResponse execute(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        // Generate JWT token
        String token = jwtPort.generateToken(user);

        // Build response
        return new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()),
            jwtExpiration
        );
    }
}
