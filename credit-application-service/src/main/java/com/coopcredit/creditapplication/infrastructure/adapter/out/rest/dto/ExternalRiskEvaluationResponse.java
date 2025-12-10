package com.coopcredit.creditapplication.infrastructure.adapter.out.rest.dto;

import java.util.List;

/**
 * DTO for external risk evaluation response.
 */
public class ExternalRiskEvaluationResponse {
    
    private String documentNumber;
    private Integer creditScore;
    private String riskLevel;
    private String recommendation;
    private List<String> riskFactors;
    
    public ExternalRiskEvaluationResponse() {}
    
    // Getters and Setters
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public Integer getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    public List<String> getRiskFactors() {
        return riskFactors;
    }
    
    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }
}
