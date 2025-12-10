package com.coopcredit.creditapplication.domain.port.out;

import java.math.BigDecimal;

/**
 * Output Port: External Risk Evaluation Service
 * Interface for communication with risk-central-mock-service.
 * Decouples domain from external service implementation.
 */
public interface RiskEvaluationPort {
    
    RiskEvaluationResponse evaluateRisk(RiskEvaluationRequest request);
    
    /**
     * DTO for risk evaluation request.
     */
    class RiskEvaluationRequest {
        private String documentNumber;
        private BigDecimal requestedAmount;
        private BigDecimal monthlyIncome;
        
        public RiskEvaluationRequest(String documentNumber, BigDecimal requestedAmount, BigDecimal monthlyIncome) {
            this.documentNumber = documentNumber;
            this.requestedAmount = requestedAmount;
            this.monthlyIncome = monthlyIncome;
        }
        
        public String getDocumentNumber() {
            return documentNumber;
        }
        
        public BigDecimal getRequestedAmount() {
            return requestedAmount;
        }
        
        public BigDecimal getMonthlyIncome() {
            return monthlyIncome;
        }
    }
    
    /**
     * DTO for risk evaluation response.
     */
    class RiskEvaluationResponse {
        private Integer score;
        private String recommendation;
        private String riskLevel;
        private String externalReference;
        
        public RiskEvaluationResponse() {
        }
        
        public RiskEvaluationResponse(Integer score, String recommendation, String riskLevel) {
            this.score = score;
            this.recommendation = recommendation;
            this.riskLevel = riskLevel;
        }
        
        public Integer getScore() {
            return score;
        }
        
        public void setScore(Integer score) {
            this.score = score;
        }
        
        public String getRecommendation() {
            return recommendation;
        }
        
        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
        
        public String getRiskLevel() {
            return riskLevel;
        }
        
        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }
        
        public String getExternalReference() {
            return externalReference;
        }
        
        public void setExternalReference(String externalReference) {
            this.externalReference = externalReference;
        }
    }
}
