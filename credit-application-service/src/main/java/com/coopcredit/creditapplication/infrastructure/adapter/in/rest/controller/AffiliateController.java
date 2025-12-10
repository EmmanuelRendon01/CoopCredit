package com.coopcredit.creditapplication.infrastructure.adapter.in.rest.controller;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.dto.UpdateAffiliateRequest;
import com.coopcredit.creditapplication.application.port.in.GetAffiliateByIdUseCase;
import com.coopcredit.creditapplication.application.port.in.GetAllAffiliatesUseCase;
import com.coopcredit.creditapplication.application.port.in.GetCurrentAffiliateUseCase;
import com.coopcredit.creditapplication.application.port.in.UpdateAffiliateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for affiliate management operations.
 * Provides CRUD endpoints for affiliates.
 */
@RestController
@RequestMapping("/api/affiliates")
@Tag(name = "Affiliates", description = "Endpoints for managing affiliate information")
@SecurityRequirement(name = "bearerAuth")
public class AffiliateController {

    private static final Logger logger = LoggerFactory.getLogger(AffiliateController.class);

    private final GetAffiliateByIdUseCase getAffiliateByIdUseCase;
    private final GetAllAffiliatesUseCase getAllAffiliatesUseCase;
    private final UpdateAffiliateUseCase updateAffiliateUseCase;
    private final GetCurrentAffiliateUseCase getCurrentAffiliateUseCase;

    public AffiliateController(GetAffiliateByIdUseCase getAffiliateByIdUseCase,
                              GetAllAffiliatesUseCase getAllAffiliatesUseCase,
                              UpdateAffiliateUseCase updateAffiliateUseCase,
                              GetCurrentAffiliateUseCase getCurrentAffiliateUseCase) {
        this.getAffiliateByIdUseCase = getAffiliateByIdUseCase;
        this.getAllAffiliatesUseCase = getAllAffiliatesUseCase;
        this.updateAffiliateUseCase = updateAffiliateUseCase;
        this.getCurrentAffiliateUseCase = getCurrentAffiliateUseCase;
    }

    /**
     * Retrieves the current authenticated affiliate.
     *
     * @return current affiliate information
     */
    @Operation(
        summary = "Get current affiliate",
        description = "Retrieves detailed information about the currently authenticated affiliate."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Current affiliate retrieved",
            content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Affiliate not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('AFILIADO')")
    public ResponseEntity<AffiliateResponse> getCurrentAffiliate() {
        logger.info("GET /api/affiliates/me - Retrieving current affiliate");
        
        AffiliateResponse response = getCurrentAffiliateUseCase.execute();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a single affiliate by ID.
     *
     * @param id the affiliate ID
     * @return affiliate information
     */
    @Operation(
        summary = "Get affiliate by ID",
        description = "Retrieves detailed information about a specific affiliate including " +
                     "months as member and total credit applications."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Affiliate found",
            content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Affiliate not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('AFILIADO') or hasRole('ANALISTA') or hasRole('ADMIN')")
    public ResponseEntity<AffiliateResponse> getAffiliateById(@PathVariable Long id) {
        logger.info("GET /api/affiliates/{} - Retrieving affiliate", id);
        
        AffiliateResponse response = getAffiliateByIdUseCase.execute(id);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all affiliates.
     *
     * @return list of all affiliates
     */
    @Operation(
        summary = "Get all affiliates",
        description = "Retrieves a list of all affiliates in the system. " +
                     "Restricted to ANALYST and ADMIN roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Affiliates retrieved successfully",
            content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMIN')")
    public ResponseEntity<List<AffiliateResponse>> getAllAffiliates() {
        logger.info("GET /api/affiliates - Retrieving all affiliates");
        
        List<AffiliateResponse> response = getAllAffiliatesUseCase.execute();
        
        logger.info("Retrieved {} affiliates", response.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Updates affiliate information.
     *
     * @param id the affiliate ID
     * @param request the update request
     * @return updated affiliate information
     */
    @Operation(
        summary = "Update affiliate",
        description = "Updates editable affiliate information (name, email, phone, salary, status). " +
                     "Document number cannot be changed. Email must remain unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Affiliate updated successfully",
            content = @Content(schema = @Schema(implementation = AffiliateResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Affiliate not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Business rule violation (e.g., email already exists)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMIN')")
    public ResponseEntity<AffiliateResponse> updateAffiliate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAffiliateRequest request) {
        
        logger.info("PUT /api/affiliates/{} - Updating affiliate", id);
        
        AffiliateResponse response = updateAffiliateUseCase.execute(id, request);
        
        return ResponseEntity.ok(response);
    }
}
