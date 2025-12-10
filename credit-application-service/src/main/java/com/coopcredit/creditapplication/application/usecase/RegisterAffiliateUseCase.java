package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AuthResponse;
import com.coopcredit.creditapplication.application.dto.RegisterRequest;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.RoleName;
import com.coopcredit.creditapplication.domain.model.User;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.domain.port.out.JwtPort;
import com.coopcredit.creditapplication.domain.port.out.PasswordEncoderPort;
import com.coopcredit.creditapplication.domain.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Use Case: Register new affiliate user.
 * Creates both User and Affiliate entities with ROLE_AFILIADO.
 */
@Service
@Transactional
public class RegisterAffiliateUseCase {

    private final UserRepositoryPort userRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public RegisterAffiliateUseCase(UserRepositoryPort userRepository,
                                    AffiliateRepositoryPort affiliateRepository,
                                    PasswordEncoderPort passwordEncoder,
                                    JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.affiliateRepository = affiliateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
    }

    /**
     * Executes affiliate registration.
     *
     * @param request registration request data
     * @return authentication response with JWT token
     * @throws IllegalArgumentException if validation fails
     */
    public AuthResponse execute(RegisterRequest request) {
        // Validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        
        if (affiliateRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new IllegalArgumentException("Document number already exists: " + request.getDocumentNumber());
        }

        // Validate business rules
        if (request.getSalary().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero");
        }

        // Create Affiliate
        Affiliate affiliate = new Affiliate(
            request.getDocumentType(),
            request.getDocumentNumber(),
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhone(),
            request.getSalary()
        );
        
        Affiliate savedAffiliate = affiliateRepository.save(affiliate);

        // Create User
        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail()
        );
        
        user.setAffiliate(savedAffiliate);
        
        // Assign ROLE_AFILIADO
        userRepository.findRoleByName(RoleName.ROLE_AFILIADO)
            .ifPresent(user::addRole);
        
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtPort.generateToken(savedUser);
        
        // Build response
        return new AuthResponse(
            token,
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()),
            jwtExpiration
        );
    }
}
