package com.coopcredit.creditapplication.infrastructure.adapter.out.rest.dto;

import java.math.BigDecimal;

/**
 * DTO for external risk evaluation request.
 */
public class ExternalRiskEvaluationRequest {
    
    private String documentNumber;
    private BigDecimal requestedAmount;
    private BigDecimal monthlyIncome;
    private BigDecimal currentDebt;
    private Integer termMonths;
    
    public ExternalRiskEvaluationRequest() {}
    
    public ExternalRiskEvaluationRequest(String documentNumber, BigDecimal requestedAmount,
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
