package com.coopcredit.creditapplication.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Domain entity: Credit Application
 * Pure POJO without infrastructure dependencies.
 * Represents a credit request made by an affiliate.
 * 
 * Business Rules:
 * - Maximum amount: affiliate's salary × 10
 * - Debt-to-income ratio must not exceed 40%
 * - Automatic status assignment based on risk score:
 * - score >= 700: APPROVED
 * - score < 400: REJECTED
 * - 400-699: IN_REVIEW
 */
public class CreditApplication {

    private Long id;
    private Affiliate affiliate;
    private BigDecimal requestedAmount;
    private String purpose;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private BigDecimal monthlyIncome;
    private Integer requestedTermMonths;
    private Integer termMonths; // Alias for requestedTermMonths
    private BigDecimal interestRate;
    private BigDecimal currentDebt;
    private LocalDateTime evaluationDate;
    private String evaluationComments;

    @JsonManagedReference
    private RiskEvaluation riskEvaluation;

    public CreditApplication() {
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    public CreditApplication(Affiliate affiliate, BigDecimal requestedAmount,
            String purpose, Integer requestedTermMonths, BigDecimal monthlyIncome) {
        this();
        this.affiliate = affiliate;
        this.requestedAmount = requestedAmount;
        this.purpose = purpose;
        this.requestedTermMonths = requestedTermMonths;
        this.monthlyIncome = monthlyIncome;
    }

    // Business Logic

    /**
     * Calculates the monthly payment using the amortization formula.
     * 
     * @param annualInterestRate annual interest rate as percentage (e.g., 12.5 for
     *                           12.5%)
     * @return monthly payment amount rounded to 2 decimal places
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal annualInterestRate) {
        double monthlyRate = annualInterestRate.doubleValue() / 12 / 100;
        double amount = requestedAmount.doubleValue();
        int months = requestedTermMonths;

        if (monthlyRate == 0) {
            return BigDecimal.valueOf(amount / months).setScale(2, RoundingMode.HALF_UP);
        }

        double payment = amount * (monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);

        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the debt-to-income ratio.
     * 
     * @param monthlyPayment the calculated monthly payment
     * @return ratio of payment to income (0.0 to 1.0+)
     */
    public BigDecimal getDebtToIncomeRatio(BigDecimal monthlyPayment) {
        if (monthlyIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        return monthlyPayment.divide(monthlyIncome, 4, RoundingMode.HALF_UP);
    }

    /**
     * Checks if the credit would exceed the maximum allowed debt ratio.
     * 
     * @param maxRatio           maximum allowed debt-to-income ratio (e.g., 0.40
     *                           for 40%)
     * @param annualInterestRate annual interest rate for payment calculation
     * @return true if debt ratio would be exceeded
     */
    public boolean exceedsDebtRatio(BigDecimal maxRatio, BigDecimal annualInterestRate) {
        BigDecimal payment = calculateMonthlyPayment(annualInterestRate);
        BigDecimal ratio = getDebtToIncomeRatio(payment);
        return ratio.compareTo(maxRatio) > 0;
    }

    /**
     * Approves the credit application.
     */
    public void approve() {
        this.status = ApplicationStatus.APPROVED;
    }

    /**
     * Rejects the credit application.
     */
    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    /**
     * Marks the application for manual review.
     */
    public void markForReview() {
        this.status = ApplicationStatus.IN_REVIEW;
    }

    /**
     * Sets the risk evaluation and automatically updates application status.
     * 
     * @param evaluation the risk evaluation result
     */
    public void setRiskEvaluation(RiskEvaluation evaluation) {
        this.riskEvaluation = evaluation;
        if (evaluation != null) {
            evaluation.setCreditApplication(this);
            if (evaluation.getScore() != null) {
                updateStatusBasedOnScore(evaluation.getScore());
            }
        }
    }

    /**
     * Updates application status based on risk score.
     * 
     * @param score the risk score (0-1000)
     */
    private void updateStatusBasedOnScore(int score) {
        if (score >= 700) {
            approve();
        } else if (score < 400) {
            reject();
        } else {
            markForReview();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Affiliate getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Affiliate affiliate) {
        this.affiliate = affiliate;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Integer getRequestedTermMonths() {
        return requestedTermMonths;
    }

    public void setRequestedTermMonths(Integer requestedTermMonths) {
        this.requestedTermMonths = requestedTermMonths;
    }

    public RiskEvaluation getRiskEvaluation() {
        return riskEvaluation;
    }

    public Integer getTermMonths() {
        return termMonths != null ? termMonths : requestedTermMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
        this.requestedTermMonths = termMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getCurrentDebt() {
        return currentDebt;
    }

    public void setCurrentDebt(BigDecimal currentDebt) {
        this.currentDebt = currentDebt;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getEvaluationComments() {
        return evaluationComments;
    }

    public void setEvaluationComments(String evaluationComments) {
        this.evaluationComments = evaluationComments;
    }
}
