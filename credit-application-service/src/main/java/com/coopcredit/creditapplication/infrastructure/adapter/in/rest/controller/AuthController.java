package com.coopcredit.creditapplication.infrastructure.adapter.in.rest.controller;

import com.coopcredit.creditapplication.application.dto.AuthResponse;
import com.coopcredit.creditapplication.application.dto.LoginRequest;
import com.coopcredit.creditapplication.application.dto.RegisterRequest;
import com.coopcredit.creditapplication.application.usecase.AuthenticateUserUseCase;
import com.coopcredit.creditapplication.application.usecase.RegisterAffiliateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterAffiliateUseCase registerAffiliateUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    /**
     * Register new affiliate user.
     *
     * @param request registration data
     * @return authentication response with JWT token
     */
    @Operation(
        summary = "Register new affiliate",
        description = "Creates a new affiliate account and returns JWT token for authentication. " +
                     "Default role: ROLE_AFILIADO. Email and document number must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Affiliate successfully registered",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - invalid input data",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Business rule violation - duplicate email or document",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerAffiliateUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param request login credentials
     * @return authentication response with JWT token
     */
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user with username and password, returns JWT token valid for 24 hours"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authenticateUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
