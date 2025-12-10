package com.coopcredit.creditapplication.domain.validation;

import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.infrastructure.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Business rules validator for credit applications.
 * Contains all domain-level validation logic.
 */
public class BusinessValidator {
    
    // Business rule constants
    private static final int MIN_AFFILIATION_MONTHS = 6;
    private static final BigDecimal MAX_DEBT_TO_INCOME_RATIO = new BigDecimal("0.50");
    private static final BigDecimal MIN_CREDIT_AMOUNT = new BigDecimal("1000000");
    private static final BigDecimal MAX_CREDIT_AMOUNT = new BigDecimal("50000000");
    private static final int MIN_TERM_MONTHS = 6;
    private static final int MAX_TERM_MONTHS = 60;
    private static final int MIN_SCORE_FOR_AUTO_APPROVAL = 700;
    private static final int MAX_SCORE_FOR_AUTO_REJECTION = 300;
    
    /**
     * Validates that an affiliate meets minimum affiliation requirements.
     *
     * @param affiliate the affiliate to validate
     * @throws BusinessException if validation fails
     */
    public static void validateAffiliationTime(Affiliate affiliate) {
        if (affiliate.getAffiliationDate() == null) {
            throw new BusinessException(
                "AFFILIATION_DATE_REQUIRED",
                "Affiliation date is required to process credit applications"
            );
        }
        
        long monthsAffiliated = ChronoUnit.MONTHS.between(
            affiliate.getAffiliationDate(),
            LocalDateTime.now()
        );
        
        if (monthsAffiliated < MIN_AFFILIATION_MONTHS) {
            throw new BusinessException(
                "INSUFFICIENT_AFFILIATION_TIME",
                String.format("Affiliate must have at least %d months of affiliation. Current: %d months",
                    MIN_AFFILIATION_MONTHS, monthsAffiliated)
            );
        }
    }
    
    /**
     * Validates credit amount is within allowed limits.
     *
     * @param amount the requested credit amount
     * @throws BusinessException if amount is out of range
     */
    public static void validateCreditAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(
                "AMOUNT_REQUIRED",
                "Credit amount is required"
            );
        }
        
        if (amount.compareTo(MIN_CREDIT_AMOUNT) < 0) {
            throw new BusinessException(
                "AMOUNT_TOO_LOW",
                String.format("Minimum credit amount is $%s", MIN_CREDIT_AMOUNT)
            );
        }
        
        if (amount.compareTo(MAX_CREDIT_AMOUNT) > 0) {
            throw new BusinessException(
                "AMOUNT_TOO_HIGH",
                String.format("Maximum credit amount is $%s", MAX_CREDIT_AMOUNT)
            );
        }
    }
    
    /**
     * Validates credit term in months.
     *
     * @param termMonths the requested term in months
     * @throws BusinessException if term is out of range
     */
    public static void validateCreditTerm(Integer termMonths) {
        if (termMonths == null) {
            throw new BusinessException(
                "TERM_REQUIRED",
                "Credit term is required"
            );
        }
        
        if (termMonths < MIN_TERM_MONTHS) {
            throw new BusinessException(
                "TERM_TOO_SHORT",
                String.format("Minimum credit term is %d months", MIN_TERM_MONTHS)
            );
        }
        
        if (termMonths > MAX_TERM_MONTHS) {
            throw new BusinessException(
                "TERM_TOO_LONG",
                String.format("Maximum credit term is %d months", MAX_TERM_MONTHS)
            );
        }
    }
    
    /**
     * Validates debt-to-income ratio.
     *
     * @param application the credit application
     * @throws BusinessException if debt ratio exceeds limit
     */
    public static void validateDebtRatio(CreditApplication application) {
        if (application.getCurrentDebt() == null || application.getCurrentDebt().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                "INVALID_CURRENT_DEBT",
                "Current debt must be zero or greater"
            );
        }
        
        BigDecimal monthlyIncome = application.getMonthlyIncome();
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                "INVALID_MONTHLY_INCOME",
                "Monthly income must be greater than zero"
            );
        }
        
        BigDecimal monthlyPayment = application.calculateMonthlyPayment(application.getInterestRate());
        if (application.exceedsDebtRatio(MAX_DEBT_TO_INCOME_RATIO, application.getInterestRate())) {
            throw new BusinessException(
                "DEBT_RATIO_EXCEEDED",
                String.format("Total debt-to-income ratio exceeds maximum allowed (%.0f%%)",
                    MAX_DEBT_TO_INCOME_RATIO.multiply(new BigDecimal("100")))
            );
        }
    }
    
    /**
     * Validates affiliate can request the specified credit amount based on salary.
     *
     * @param affiliate the affiliate
     * @param requestedAmount the requested credit amount
     * @throws BusinessException if amount exceeds affiliate's limit
     */
    public static void validateAffiliateLimit(Affiliate affiliate, BigDecimal requestedAmount) {
        if (!affiliate.canRequestCredit(MIN_AFFILIATION_MONTHS)) {
            throw new BusinessException(
                "INSUFFICIENT_AFFILIATION_TIME",
                String.format("Affiliate must have at least %d months of affiliation", MIN_AFFILIATION_MONTHS)
            );
        }
        
        BigDecimal maxAllowed = affiliate.getMaxCreditAmount();
        if (requestedAmount.compareTo(maxAllowed) > 0) {
            throw new BusinessException(
                "AMOUNT_EXCEEDS_AFFILIATE_LIMIT",
                String.format("Requested amount ($%s) exceeds affiliate's maximum credit limit ($%s based on salary)",
                    requestedAmount, maxAllowed)
            );
        }
    }
    
    /**
     * Determines if a credit score requires manual review.
     *
     * @param score the credit score
     * @return true if manual review is needed
     */
    public static boolean requiresManualReview(Integer score) {
        if (score == null) {
            return true;
        }
        return score > MAX_SCORE_FOR_AUTO_REJECTION && score < MIN_SCORE_FOR_AUTO_APPROVAL;
    }
    
    /**
     * Validates affiliate has no active pending applications.
     *
     * @param hasPendingApplications true if affiliate has pending applications
     * @throws BusinessException if validation fails
     */
    public static void validateNoPendingApplications(boolean hasPendingApplications) {
        if (hasPendingApplications) {
            throw new BusinessException(
                "PENDING_APPLICATION_EXISTS",
                "Affiliate already has a pending credit application. Please wait for current application to be processed."
            );
        }
    }
    
    /**
     * Gets minimum affiliation months required.
     *
     * @return minimum months
     */
    public static int getMinAffiliationMonths() {
        return MIN_AFFILIATION_MONTHS;
    }
    
    /**
     * Gets maximum debt-to-income ratio allowed.
     *
     * @return maximum ratio as decimal
     */
    public static BigDecimal getMaxDebtRatio() {
        return MAX_DEBT_TO_INCOME_RATIO;
    }
    
    /**
     * Gets minimum credit amount.
     *
     * @return minimum amount
     */
    public static BigDecimal getMinCreditAmount() {
        return MIN_CREDIT_AMOUNT;
    }
    
    /**
     * Gets maximum credit amount.
     *
     * @return maximum amount
     */
    public static BigDecimal getMaxCreditAmount() {
        return MAX_CREDIT_AMOUNT;
    }
}
