package com.coopcredit.creditapplication.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity: Risk Evaluation
 * Pure POJO without infrastructure dependencies.
 * Represents the risk assessment result for a credit application.
 * 
 * Score ranges:
 * - 0-399: High risk (REJECT)
 * - 400-699: Medium risk (MANUAL_REVIEW)
 * - 700-1000: Low risk (APPROVE)
 */
public class RiskEvaluation {
    
    private Long id;
    
    @JsonBackReference
    private CreditApplication creditApplication;
    
    private Integer score;
    private Integer creditScore; // Alias for score
    private LocalDateTime evaluationDate;
    private String recommendation;
    private String riskLevel;
    private String externalReference;
    private List<String> riskFactors;

    public RiskEvaluation() {
        this.evaluationDate = LocalDateTime.now();
        this.riskFactors = new ArrayList<>();
    }

    public RiskEvaluation(Integer score, String recommendation, String riskLevel) {
        this();
        this.score = score;
        this.creditScore = score;
        this.recommendation = recommendation;
        this.riskLevel = riskLevel;
    }

    // Business Logic
    
    /**
     * Checks if the recommendation is to approve the credit.
     * 
     * @return true if recommendation is APPROVE
     */
    public boolean isApproved() {
        return "APPROVE".equalsIgnoreCase(recommendation);
    }

    /**
     * Checks if the recommendation is to reject the credit.
     * 
     * @return true if recommendation is REJECT
     */
    public boolean isRejected() {
        return "REJECT".equalsIgnoreCase(recommendation);
    }

    /**
     * Checks if the application needs manual review.
     * 
     * @return true if recommendation is MANUAL_REVIEW
     */
    public boolean needsManualReview() {
        return "MANUAL_REVIEW".equalsIgnoreCase(recommendation);
    }

    /**
     * Checks if the risk level is HIGH.
     * 
     * @return true if risk level is HIGH
     */
    public boolean isHighRisk() {
        return "HIGH".equalsIgnoreCase(riskLevel);
    }

    /**
     * Checks if the risk level is LOW.
     * 
     * @return true if risk level is LOW
     */
    public boolean isLowRisk() {
        return "LOW".equalsIgnoreCase(riskLevel);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplication getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplication creditApplication) {
        this.creditApplication = creditApplication;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
        this.creditScore = score; // Keep both in sync
    }

    public Integer getCreditScore() {
        return creditScore != null ? creditScore : score;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
        this.score = creditScore; // Keep both in sync
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
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

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }
}
