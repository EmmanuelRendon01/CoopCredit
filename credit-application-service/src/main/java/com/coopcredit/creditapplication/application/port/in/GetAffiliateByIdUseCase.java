package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;

/**
 * Use case interface for retrieving a single affiliate by ID.
 */
public interface GetAffiliateByIdUseCase {
    
    /**
     * Retrieves affiliate information by ID.
     *
     * @param affiliateId the affiliate ID
     * @return affiliate information
     * @throws com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException if affiliate not found
     */
    AffiliateResponse execute(Long affiliateId);
}
