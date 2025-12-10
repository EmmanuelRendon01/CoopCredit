package com.coopcredit.creditapplication.infrastructure.adapter.out.rest;

import com.coopcredit.creditapplication.application.port.out.RiskEvaluationPort;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.domain.model.RiskEvaluation;
import com.coopcredit.creditapplication.infrastructure.adapter.out.rest.dto.ExternalRiskEvaluationRequest;
import com.coopcredit.creditapplication.infrastructure.adapter.out.rest.dto.ExternalRiskEvaluationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * REST client adapter for external risk evaluation service.
 * Implements RiskEvaluationPort to call Risk Central Service.
 */
@Component
public class RiskEvaluationRestAdapter implements RiskEvaluationPort {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskEvaluationRestAdapter.class);
    
    private final RestTemplate restTemplate;
    private final String riskServiceUrl;
    
    public RiskEvaluationRestAdapter(
            RestTemplate restTemplate,
            @Value("${risk-service.url:http://localhost:8081}") String riskServiceUrl) {
        this.restTemplate = restTemplate;
        this.riskServiceUrl = riskServiceUrl;
    }
    
    @Override
    public RiskEvaluation evaluateRisk(CreditApplication application) {
        logger.debug("Calling Risk Central Service at: {}", riskServiceUrl);
        
        // Map to external request DTO
        ExternalRiskEvaluationRequest request = new ExternalRiskEvaluationRequest(
            application.getAffiliate().getDocumentNumber(),
            application.getRequestedAmount(),
            application.getMonthlyIncome(),
            application.getCurrentDebt(),
            application.getTermMonths()
        );
        
        // Call external service
        String url = riskServiceUrl + "/api/risk-evaluation/evaluate";
        logger.info("POST {} - Evaluating risk for document: {}", 
            url, request.getDocumentNumber());
        
        ExternalRiskEvaluationResponse response = restTemplate.postForObject(
            url,
            request,
            ExternalRiskEvaluationResponse.class
        );
        
        if (response == null) {
            throw new RuntimeException("Risk service returned null response");
        }
        
        logger.debug("Risk evaluation received - Score: {}, Recommendation: {}", 
            response.getCreditScore(), response.getRecommendation());
        
        // Map to domain model
        return mapToDomain(response);
    }
    
    /**
     * Maps external response to domain model.
     */
    private RiskEvaluation mapToDomain(ExternalRiskEvaluationResponse response) {
        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setCreditScore(response.getCreditScore());
        evaluation.setRiskLevel(response.getRiskLevel());
        evaluation.setRecommendation(response.getRecommendation());
        evaluation.setRiskFactors(response.getRiskFactors());
        
        return evaluation;
    }
}
