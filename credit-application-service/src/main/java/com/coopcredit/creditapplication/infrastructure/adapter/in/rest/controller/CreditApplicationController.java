package com.coopcredit.creditapplication.infrastructure.adapter.in.rest.controller;

import com.coopcredit.creditapplication.application.dto.CreditApplicationRequest;
import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapplication.application.port.in.ApproveApplicationUseCase;
import com.coopcredit.creditapplication.application.port.in.EvaluateCreditApplicationUseCase;
import com.coopcredit.creditapplication.application.port.in.GetApplicationsByAffiliateUseCase;
import com.coopcredit.creditapplication.application.port.in.RegisterCreditApplicationUseCase;
import com.coopcredit.creditapplication.application.port.in.RejectApplicationUseCase;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for credit application operations.
 * Provides endpoints for creating, evaluating, and retrieving credit
 * applications.
 */
@RestController
@RequestMapping("/api/credit-applications")
@Tag(name = "Credit Applications", description = "Endpoints for managing credit applications")
@SecurityRequirement(name = "bearerAuth")
public class CreditApplicationController {

        private static final Logger logger = LoggerFactory.getLogger(CreditApplicationController.class);

        private final RegisterCreditApplicationUseCase registerApplicationUseCase;
        private final EvaluateCreditApplicationUseCase evaluateApplicationUseCase;
        private final GetApplicationsByAffiliateUseCase getApplicationsByAffiliateUseCase;
        private final ApproveApplicationUseCase approveApplicationUseCase;
        private final RejectApplicationUseCase rejectApplicationUseCase;

        public CreditApplicationController(
                        RegisterCreditApplicationUseCase registerApplicationUseCase,
                        EvaluateCreditApplicationUseCase evaluateApplicationUseCase,
                        GetApplicationsByAffiliateUseCase getApplicationsByAffiliateUseCase,
                        ApproveApplicationUseCase approveApplicationUseCase,
                        RejectApplicationUseCase rejectApplicationUseCase) {
                this.registerApplicationUseCase = registerApplicationUseCase;
                this.evaluateApplicationUseCase = evaluateApplicationUseCase;
                this.getApplicationsByAffiliateUseCase = getApplicationsByAffiliateUseCase;
                this.approveApplicationUseCase = approveApplicationUseCase;
                this.rejectApplicationUseCase = rejectApplicationUseCase;
        }

        /**
         * Creates a new credit application for an affiliate.
         *
         * @param affiliateId the affiliate ID
         * @param request     the credit application request
         * @return the created application
         */
        @Operation(summary = "Create credit application", description = "Creates a new credit application for an affiliate. Validates business rules: "
                        +
                        "affiliate must be ACTIVE, minimum tenure, debt ratio limits.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Credit application created successfully", content = @Content(schema = @Schema(implementation = CreditApplicationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "404", description = "Affiliate not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "422", description = "Business rule violation", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        @PostMapping("/affiliates/{affiliateId}")
        @PreAuthorize("hasRole('AFILIADO') or hasRole('ANALISTA')")
        public ResponseEntity<CreditApplicationResponse> createApplication(
                        @PathVariable Long affiliateId,
                        @Valid @RequestBody CreditApplicationRequest request) {

                logger.info("POST /api/credit-applications/affiliates/{} - Creating new application", affiliateId);

                CreditApplicationResponse response = registerApplicationUseCase.execute(affiliateId, request);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Evaluates a credit application using external risk service.
         *
         * @param applicationId the application ID
         * @return the evaluated application
         */
        @Operation(summary = "Evaluate credit application", description = "Evaluates a PENDING application by calling risk-central-service, "
                        +
                        "applying business policies, and updating status to APPROVED/REJECTED. " +
                        "Restricted to ANALYST role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Application evaluated successfully", content = @Content(schema = @Schema(implementation = CreditApplicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "422", description = "Application already evaluated or invalid state", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        @PostMapping("/{applicationId}/evaluate")
        @PreAuthorize("hasRole('ANALISTA')")
        public ResponseEntity<CreditApplicationResponse> evaluateApplication(
                        @PathVariable Long applicationId) {

                logger.info("POST /api/credit-applications/{}/evaluate - Evaluating application", applicationId);

                CreditApplicationResponse response = evaluateApplicationUseCase.execute(applicationId);

                return ResponseEntity.ok(response);
        }

        /**
         * Retrieves all credit applications for a specific affiliate.
         *
         * @param affiliateId the affiliate ID
         * @return list of applications
         */
        @Operation(summary = "Get applications by affiliate", description = "Retrieves all credit applications for a specific affiliate. "
                        +
                        "Includes evaluation details if available.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully", content = @Content(schema = @Schema(implementation = CreditApplicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Affiliate not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        @GetMapping("/affiliates/{affiliateId}")
        @PreAuthorize("hasRole('AFILIADO') or hasRole('ANALISTA')")
        public ResponseEntity<List<CreditApplicationResponse>> getApplicationsByAffiliate(
                        @PathVariable Long affiliateId) {

                logger.info("GET /api/credit-applications/affiliates/{} - Retrieving applications", affiliateId);

                List<CreditApplicationResponse> applications = getApplicationsByAffiliateUseCase.execute(affiliateId);

                return ResponseEntity.ok(applications);
        }

        /**
         * Manually approves a credit application.
         *
         * @param applicationId the application ID
         * @return the approved application
         */
        @Operation(summary = "Approve credit application", description = "Manually approves a credit application. "
                        + "Restricted to ANALYST role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Application approved successfully", content = @Content(schema = @Schema(implementation = CreditApplicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        @PostMapping("/{applicationId}/approve")
        @PreAuthorize("hasRole('ANALISTA')")
        public ResponseEntity<CreditApplicationResponse> approveApplication(
                        @PathVariable Long applicationId) {

                logger.info("POST /api/credit-applications/{}/approve - Manually approving application", applicationId);

                CreditApplicationResponse response = approveApplicationUseCase.execute(applicationId);

                return ResponseEntity.ok(response);
        }

        /**
         * Manually rejects a credit application.
         *
         * @param applicationId the application ID
         * @return the rejected application
         */
        @Operation(summary = "Reject credit application", description = "Manually rejects a credit application. "
                        + "Restricted to ANALYST role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Application rejected successfully", content = @Content(schema = @Schema(implementation = CreditApplicationResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        @PostMapping("/{applicationId}/reject")
        @PreAuthorize("hasRole('ANALISTA')")
        public ResponseEntity<CreditApplicationResponse> rejectApplication(
                        @PathVariable Long applicationId) {

                logger.info("POST /api/credit-applications/{}/reject - Manually rejecting application", applicationId);

                CreditApplicationResponse response = rejectApplicationUseCase.execute(applicationId);

                return ResponseEntity.ok(response);
        }
}
