package com.coopcredit.creditapplication.application.port.out;

import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;

import java.util.List;
import java.util.Optional;

/**
 * Port OUT - Repository for credit applications.
 */
public interface CreditApplicationRepositoryPort {
    CreditApplication save(CreditApplication creditApplication);

    Optional<CreditApplication> findById(Long id);

    List<CreditApplication> findByAffiliateId(Long affiliateId);

    List<CreditApplication> findByAffiliateIdAndStatus(Long affiliateId, ApplicationStatus status);
}
