package com.coopcredit.creditapplication.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for credit application request.
 */
public class CreditApplicationRequest {
    
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "1000000", message = "Minimum credit amount is 1,000,000")
    @DecimalMax(value = "50000000", message = "Maximum credit amount is 50,000,000")
    private BigDecimal requestedAmount;
    
    @NotNull(message = "Term in months is required")
    @Min(value = 6, message = "Minimum term is 6 months")
    @Max(value = 60, message = "Maximum term is 60 months")
    private Integer termMonths;
    
    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than zero")
    @DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    private BigDecimal interestRate;
    
    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.01", message = "Monthly income must be greater than zero")
    private BigDecimal monthlyIncome;
    
    @NotNull(message = "Current debt is required")
    @DecimalMin(value = "0.00", message = "Current debt cannot be negative")
    private BigDecimal currentDebt;
    
    @Size(max = 500, message = "Purpose description cannot exceed 500 characters")
    private String purpose;
    
    // Constructors
    public CreditApplicationRequest() {}
    
    public CreditApplicationRequest(BigDecimal requestedAmount, Integer termMonths, 
                                   BigDecimal interestRate, BigDecimal monthlyIncome,
                                   BigDecimal currentDebt, String purpose) {
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.monthlyIncome = monthlyIncome;
        this.currentDebt = currentDebt;
        this.purpose = purpose;
    }
    
    // Getters and Setters
    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
    
    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }
    
    public Integer getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
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
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
