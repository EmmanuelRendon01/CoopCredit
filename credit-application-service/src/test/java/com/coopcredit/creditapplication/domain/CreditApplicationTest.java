package com.coopcredit.creditapplication.domain;

import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreditApplication domain model.
 * Tests financial calculations and status transitions.
 */
class CreditApplicationTest {

    private CreditApplication creditApplication;

    @BeforeEach
    void setUp() {
        Affiliate affiliate = new Affiliate();
        affiliate.setSalary(new BigDecimal("5000000"));

        creditApplication = new CreditApplication();
        creditApplication.setAffiliate(affiliate);
        creditApplication.setRequestedAmount(new BigDecimal("10000000"));
        creditApplication.setRequestedTermMonths(24);
        creditApplication.setMonthlyIncome(new BigDecimal("5000000"));
        creditApplication.setCurrentDebt(new BigDecimal("1000000"));
        creditApplication.setPurpose("Compra de vivienda");
    }

    @Test
    @DisplayName("Should calculate monthly payment with interest rate")
    void testCalculateMonthlyPayment() {
        // Given
        BigDecimal annualRate = new BigDecimal("12.0"); // 12% annual

        // When
        BigDecimal monthlyPayment = creditApplication.calculateMonthlyPayment(annualRate);

        // Then
        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.compareTo(BigDecimal.ZERO) > 0);
        // Payment should be more than principal only (10M / 24 ≈ 416,666)
        assertTrue(monthlyPayment.compareTo(new BigDecimal("416666")) > 0);
    }

    @Test
    @DisplayName("Should calculate higher payment for higher interest rate")
    void testCalculateMonthlyPayment_HigherRate() {
        // Given
        BigDecimal lowRate = new BigDecimal("10.0");
        BigDecimal highRate = new BigDecimal("20.0");

        // When
        BigDecimal paymentLow = creditApplication.calculateMonthlyPayment(lowRate);
        BigDecimal paymentHigh = creditApplication.calculateMonthlyPayment(highRate);

        // Then
        assertTrue(paymentHigh.compareTo(paymentLow) > 0);
    }

    @Test
    @DisplayName("Should calculate debt-to-income ratio")
    void testGetDebtToIncomeRatio() {
        // Given
        BigDecimal monthlyPayment = new BigDecimal("500000");

        // When
        BigDecimal ratio = creditApplication.getDebtToIncomeRatio(monthlyPayment);

        // Then
        // 500,000 / 5,000,000 = 0.10
        assertEquals(new BigDecimal("0.1000"), ratio);
    }

    @Test
    @DisplayName("Should detect when debt ratio exceeds maximum")
    void testExceedsDebtRatio_WhenExceeds() {
        // Given
        creditApplication.setRequestedAmount(new BigDecimal("50000000")); // Very high amount
        creditApplication.setMonthlyIncome(new BigDecimal("3000000")); // Lower income
        BigDecimal maxRatio = new BigDecimal("0.30"); // 30% limit
        BigDecimal annualRate = new BigDecimal("15.0");

        // When
        boolean exceeds = creditApplication.exceedsDebtRatio(maxRatio, annualRate);

        // Then
        assertTrue(exceeds);
    }

    @Test
    @DisplayName("Should NOT exceed when debt ratio is within limit")
    void testExceedsDebtRatio_WhenWithinLimit() {
        // Given
        creditApplication.setRequestedAmount(new BigDecimal("5000000")); // Low amount
        BigDecimal maxRatio = new BigDecimal("0.40"); // 40%
        BigDecimal annualRate = new BigDecimal("12.0");

        // When
        boolean exceeds = creditApplication.exceedsDebtRatio(maxRatio, annualRate);

        // Then
        assertFalse(exceeds);
    }

    @Test
    @DisplayName("Should approve credit application")
    void testApprove() {
        // Given
        creditApplication.setStatus(ApplicationStatus.PENDING);

        // When
        creditApplication.approve();

        // Then
        assertEquals(ApplicationStatus.APPROVED, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Should reject credit application")
    void testReject() {
        // Given
        creditApplication.setStatus(ApplicationStatus.PENDING);

        // When
        creditApplication.reject();

        // Then
        assertEquals(ApplicationStatus.REJECTED, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Should mark application for review")
    void testMarkForReview() {
        // Given
        creditApplication.setStatus(ApplicationStatus.PENDING);

        // When
        creditApplication.markForReview();

        // Then
        assertEquals(ApplicationStatus.IN_REVIEW, creditApplication.getStatus());
    }

    @Test
    @DisplayName("Should initialize with PENDING status")
    void testDefaultStatus() {
        // Given
        CreditApplication newApp = new CreditApplication();

        // When
        ApplicationStatus status = newApp.getStatus();

        // Then
        assertEquals(ApplicationStatus.PENDING, status);
    }

    @Test
    @DisplayName("Should calculate payment with zero interest rate")
    void testCalculateMonthlyPayment_ZeroInterest() {
        // Given
        BigDecimal zeroRate = BigDecimal.ZERO;

        // When
        BigDecimal payment = creditApplication.calculateMonthlyPayment(zeroRate);

        // Then
        // 10,000,000 / 24 = 416,666.67
        assertEquals(new BigDecimal("416666.67"), payment);
    }
}
