package com.coopcredit.riskcentral.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for risk evaluation request.
 */
public class RiskEvaluationRequest {
    
    @NotBlank(message = "Document number is required")
    private String documentNumber;
    
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal requestedAmount;
    
    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.01", message = "Monthly income must be greater than zero")
    private BigDecimal monthlyIncome;
    
    @NotNull(message = "Current debt is required")
    @DecimalMin(value = "0.00", message = "Current debt cannot be negative")
    private BigDecimal currentDebt;
    
    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer termMonths;
    
    // Constructors
    public RiskEvaluationRequest() {}
    
    public RiskEvaluationRequest(String documentNumber, BigDecimal requestedAmount, 
                                BigDecimal monthlyIncome, BigDecimal currentDebt, 
                                Integer termMonths) {
        this.documentNumber = documentNumber;
        this.requestedAmount = requestedAmount;
        this.monthlyIncome = monthlyIncome;
        this.currentDebt = currentDebt;
        this.termMonths = termMonths;
    }
    
    // Getters and Setters
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
    
    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }
    
    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }
    
    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
    
    public BigDecimal getCurrentDebt() {
        return currentDebt;
    }
    
    public void setCurrentDebt(BigDecimal currentDebt) {
        this.currentDebt = currentDebt;
    }
    
    public Integer getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }
}
