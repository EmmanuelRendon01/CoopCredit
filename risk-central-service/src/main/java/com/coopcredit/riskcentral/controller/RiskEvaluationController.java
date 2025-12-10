package com.coopcredit.riskcentral.controller;

import com.coopcredit.riskcentral.dto.RiskEvaluationRequest;
import com.coopcredit.riskcentral.dto.RiskEvaluationResponse;
import com.coopcredit.riskcentral.service.RiskEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for risk evaluation endpoints.
 */
@RestController
@RequestMapping("/api/risk-evaluation")
public class RiskEvaluationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskEvaluationController.class);
    
    private final RiskEvaluationService riskEvaluationService;
    
    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }
    
    /**
     * Evaluates credit risk for an applicant.
     *
     * @param request the evaluation request
     * @return the evaluation response with score and recommendation
     */
    @PostMapping("/evaluate")
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(
            @Valid @RequestBody RiskEvaluationRequest request) {
        
        logger.info("POST /api/risk-evaluation/evaluate - Document: {}", request.getDocumentNumber());
        
        RiskEvaluationResponse response = riskEvaluationService.evaluate(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint.
     *
     * @return simple status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Risk Central Service is running");
    }
}
