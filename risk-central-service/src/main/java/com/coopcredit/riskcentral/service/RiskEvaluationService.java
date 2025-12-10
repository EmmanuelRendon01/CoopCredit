package com.coopcredit.riskcentral.service;

import com.coopcredit.riskcentral.dto.RiskEvaluationRequest;
import com.coopcredit.riskcentral.dto.RiskEvaluationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for calculating credit risk scores.
 * Uses deterministic algorithm based on financial indicators.
 */
@Service
public class RiskEvaluationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskEvaluationService.class);
    
    // Score ranges
    private static final int MAX_SCORE = 1000;
    private static final int MIN_SCORE = 0;
    
    // Risk thresholds
    private static final BigDecimal EXCELLENT_DEBT_RATIO = new BigDecimal("0.20");
    private static final BigDecimal GOOD_DEBT_RATIO = new BigDecimal("0.35");
    private static final BigDecimal ACCEPTABLE_DEBT_RATIO = new BigDecimal("0.50");
    
    private static final BigDecimal LOW_INCOME_THRESHOLD = new BigDecimal("2000000");
    private static final BigDecimal MEDIUM_INCOME_THRESHOLD = new BigDecimal("5000000");
    
    private static final BigDecimal SMALL_LOAN_THRESHOLD = new BigDecimal("5000000");
    private static final BigDecimal MEDIUM_LOAN_THRESHOLD = new BigDecimal("20000000");
    
    /**
     * Evaluates credit risk based on applicant's financial information.
     * Uses document number hash as seed for consistent responses.
     *
     * @param request the evaluation request
     * @return the risk evaluation response with score and recommendation
     */
    public RiskEvaluationResponse evaluate(RiskEvaluationRequest request) {
        logger.info("Evaluating risk for document: {}", request.getDocumentNumber());
        
        List<String> riskFactors = new ArrayList<>();
        
        // Use document hash as seed for consistent base score
        int baseScore = calculateBaseScoreFromDocument(request.getDocumentNumber());
        
        // 1. Debt-to-Income Ratio Analysis (weight: 40%)
        BigDecimal debtRatio = calculateDebtRatio(request);
        int debtRatioScore = calculateDebtRatioScore(debtRatio, riskFactors);
        logger.debug("Debt ratio: {}, score contribution: {}", debtRatio, debtRatioScore);
        
        // 2. Income Level Analysis (weight: 25%)
        int incomeScore = calculateIncomeScore(request.getMonthlyIncome(), riskFactors);
        logger.debug("Income score contribution: {}", incomeScore);
        
        // 3. Loan Amount Analysis (weight: 20%)
        int loanAmountScore = calculateLoanAmountScore(
            request.getRequestedAmount(), 
            request.getMonthlyIncome(), 
            riskFactors
        );
        logger.debug("Loan amount score contribution: {}", loanAmountScore);
        
        // 4. Term Analysis (weight: 15%)
        int termScore = calculateTermScore(request.getTermMonths(), riskFactors);
        logger.debug("Term score contribution: {}", termScore);
        
        // Calculate final score
        int finalScore = baseScore + debtRatioScore + incomeScore + loanAmountScore + termScore;
        finalScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, finalScore));
        
        // Determine risk level and recommendation
        String riskLevel = determineRiskLevel(finalScore);
        String recommendation = determineRecommendation(finalScore);
        
        logger.info("Final evaluation - Score: {}, Risk: {}, Recommendation: {}", 
            finalScore, riskLevel, recommendation);
        
        return new RiskEvaluationResponse(
            request.getDocumentNumber(),
            finalScore,
            riskLevel,
            recommendation,
            riskFactors
        );
    }
    
    /**
     * Calculates a consistent base score from document number using hash.
     * Same document will always get the same base score.
     *
     * @param documentNumber the applicant's document number
     * @return base score between 400-600
     */
    private int calculateBaseScoreFromDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.isEmpty()) {
            return 500; // Default
        }
        
        // Use Java's hashCode for consistent hashing
        int hash = documentNumber.hashCode();
        
        // Convert hash to positive value and map to range 400-600
        int positiveHash = Math.abs(hash);
        int baseScore = 400 + (positiveHash % 201); // Range: 400-600
        
        logger.debug("Document {} hashed to base score: {}", documentNumber, baseScore);
        return baseScore;
    }
    
    /**
     * Calculates debt-to-income ratio including the new loan.
     */
    private BigDecimal calculateDebtRatio(RiskEvaluationRequest request) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(
            request.getRequestedAmount(),
            request.getTermMonths()
        );
        
        BigDecimal totalMonthlyDebt = request.getCurrentDebt().add(monthlyPayment);
        
        return totalMonthlyDebt.divide(request.getMonthlyIncome(), 4, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates monthly payment for a loan (simple formula).
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer months) {
        // Simplified calculation: amount / months (ignoring interest for risk calc)
        return amount.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Scores based on debt-to-income ratio (40% weight = +/- 200 points).
     */
    private int calculateDebtRatioScore(BigDecimal debtRatio, List<String> riskFactors) {
        if (debtRatio.compareTo(EXCELLENT_DEBT_RATIO) <= 0) {
            riskFactors.add("Excellent debt-to-income ratio");
            return 200; // Excellent
        } else if (debtRatio.compareTo(GOOD_DEBT_RATIO) <= 0) {
            riskFactors.add("Good debt-to-income ratio");
            return 100; // Good
        } else if (debtRatio.compareTo(ACCEPTABLE_DEBT_RATIO) <= 0) {
            riskFactors.add("Acceptable debt-to-income ratio");
            return 0; // Acceptable
        } else {
            riskFactors.add("High debt-to-income ratio: " + debtRatio.multiply(new BigDecimal("100")).intValue() + "%");
            return -200; // High risk
        }
    }
    
    /**
     * Scores based on income level (25% weight = +/- 125 points).
     */
    private int calculateIncomeScore(BigDecimal monthlyIncome, List<String> riskFactors) {
        if (monthlyIncome.compareTo(MEDIUM_INCOME_THRESHOLD) >= 0) {
            riskFactors.add("High income level");
            return 125;
        } else if (monthlyIncome.compareTo(LOW_INCOME_THRESHOLD) >= 0) {
            riskFactors.add("Medium income level");
            return 50;
        } else {
            riskFactors.add("Low income level");
            return -75;
        }
    }
    
    /**
     * Scores based on loan amount relative to income (20% weight = +/- 100 points).
     */
    private int calculateLoanAmountScore(BigDecimal loanAmount, BigDecimal monthlyIncome, 
                                        List<String> riskFactors) {
        BigDecimal loanToIncomeRatio = loanAmount.divide(
            monthlyIncome.multiply(new BigDecimal("12")), 
            2, 
            RoundingMode.HALF_UP
        );
        
        if (loanToIncomeRatio.compareTo(new BigDecimal("2")) <= 0) {
            riskFactors.add("Conservative loan amount");
            return 100;
        } else if (loanToIncomeRatio.compareTo(new BigDecimal("4")) <= 0) {
            riskFactors.add("Moderate loan amount");
            return 0;
        } else {
            riskFactors.add("High loan amount relative to annual income");
            return -100;
        }
    }
    
    /**
     * Scores based on loan term (15% weight = +/- 75 points).
     */
    private int calculateTermScore(Integer termMonths, List<String> riskFactors) {
        if (termMonths <= 12) {
            riskFactors.add("Short-term loan (lower risk)");
            return 75;
        } else if (termMonths <= 36) {
            riskFactors.add("Medium-term loan");
            return 25;
        } else {
            riskFactors.add("Long-term loan (higher risk)");
            return -50;
        }
    }
    
    /**
     * Determines risk level based on score.
     */
    private String determineRiskLevel(int score) {
        if (score >= 700) {
            return "LOW";
        } else if (score >= 500) {
            return "MEDIUM";
        } else if (score >= 300) {
            return "HIGH";
        } else {
            return "VERY_HIGH";
        }
    }
    
    /**
     * Determines recommendation based on score.
     */
    private String determineRecommendation(int score) {
        if (score >= 700) {
            return "APPROVE";
        } else if (score >= 300) {
            return "MANUAL_REVIEW";
        } else {
            return "REJECT";
        }
    }
}
