package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.dto.UpdateAffiliateRequest;

/**
 * Use case interface for updating affiliate information.
 * 
 * Business Rules:
 * - Only editable fields can be updated (name, email, phone, salary, status)
 * - Email must remain unique across all affiliates
 * - Cannot change document number
 */
public interface UpdateAffiliateUseCase {
    
    /**
     * Updates affiliate information.
     *
     * @param affiliateId the affiliate ID to update
     * @param request the update request with new values
     * @return updated affiliate information
     * @throws com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException if affiliate not found
     * @throws com.coopcredit.creditapplication.infrastructure.exception.BusinessException if business rules violated
     */
    AffiliateResponse execute(Long affiliateId, UpdateAffiliateRequest request);
}
