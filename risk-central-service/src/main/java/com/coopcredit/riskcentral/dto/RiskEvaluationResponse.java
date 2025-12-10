package com.coopcredit.riskcentral.dto;

import java.util.List;

/**
 * DTO for risk evaluation response.
 */
public class RiskEvaluationResponse {
    
    private String documentNumber;
    private Integer creditScore;
    private String riskLevel;
    private String recommendation;
    private List<String> riskFactors;
    
    // Constructors
    public RiskEvaluationResponse() {}
    
    public RiskEvaluationResponse(String documentNumber, Integer creditScore, 
                                 String riskLevel, String recommendation, 
                                 List<String> riskFactors) {
        this.documentNumber = documentNumber;
        this.creditScore = creditScore;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
        this.riskFactors = riskFactors;
    }
    
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
