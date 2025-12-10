package com.coopcredit.creditapplication.domain;

import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.AffiliateStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Affiliate domain model.
 * Tests pure business logic without framework dependencies.
 */
class AffiliateTest {

    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        affiliate = new Affiliate();
        affiliate.setDocumentType("CC");
        affiliate.setDocumentNumber("1017654321");
        affiliate.setFirstName("Juan");
        affiliate.setLastName("Pérez");
        affiliate.setEmail("juan.perez@example.com");
        affiliate.setPhone("3001234567");
        affiliate.setSalary(new BigDecimal("5000000"));
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(6));
    }

    @Test
    @DisplayName("Should return true when affiliate is ACTIVE")
    void testIsActive_WhenActive() {
        // Given
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        // When
        boolean isActive = affiliate.isActive();

        // Then
        assertTrue(isActive);
    }

    @Test
    @DisplayName("Should return false when affiliate is INACTIVE")
    void testIsActive_WhenInactive() {
        // Given
        affiliate.setStatus(AffiliateStatus.INACTIVE);

        // When
        boolean isActive = affiliate.isActive();

        // Then
        assertFalse(isActive);
    }

    @Test
    @DisplayName("Should calculate max credit as salary * 10 (default multiplier)")
    void testGetMaxCreditAmount_DefaultMultiplier() {
        // Given
        affiliate.setSalary(new BigDecimal("5000000"));

        // When
        BigDecimal maxCredit = affiliate.getMaxCreditAmount();

        // Then
        assertEquals(new BigDecimal("50000000"), maxCredit);
    }

    @Test
    @DisplayName("Should calculate max credit with custom multiplier")
    void testGetMaxCreditAmount_CustomMultiplier() {
        // Given
        affiliate.setSalary(new BigDecimal("5000000"));

        // When
        BigDecimal maxCredit = affiliate.getMaxCreditAmount(5);

        // Then
        assertEquals(new BigDecimal("25000000"), maxCredit);
    }

    @Test
    @DisplayName("Should calculate months as affiliate correctly")
    void testGetMonthsAsAffiliate() {
        // Given
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(12));

        // When
        long months = affiliate.getMonthsAsAffiliate();

        // Then
        assertEquals(12, months);
    }

    @Test
    @DisplayName("Should allow credit request when ACTIVE and meets minimum tenure")
    void testCanRequestCredit_WhenActiveAndMeetsTenure() {
        // Given
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(6));

        // When
        boolean canRequest = affiliate.canRequestCredit(3);

        // Then
        assertTrue(canRequest);
    }

    @Test
    @DisplayName("Should NOT allow credit request when INACTIVE")
    void testCanRequestCredit_WhenInactive() {
        // Given
        affiliate.setStatus(AffiliateStatus.INACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(12));

        // When
        boolean canRequest = affiliate.canRequestCredit(3);

        // Then
        assertFalse(canRequest);
    }

    @Test
    @DisplayName("Should NOT allow credit request when tenure insufficient")
    void testCanRequestCredit_InsufficientTenure() {
        // Given
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(2));

        // When
        boolean canRequest = affiliate.canRequestCredit(6);

        // Then
        assertFalse(canRequest);
    }

    @Test
    @DisplayName("Should allow credit request exactly at minimum tenure")
    void testCanRequestCredit_ExactlyAtThreshold() {
        // Given
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(3));

        // When
        boolean canRequest = affiliate.canRequestCredit(3);

        // Then
        assertTrue(canRequest);
    }
}
