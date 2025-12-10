package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;

/**
 * Use case for retrieving the current authenticated affiliate.
 */
public interface GetCurrentAffiliateUseCase {
    
    /**
     * Gets the current authenticated affiliate information.
     * 
     * @return the current affiliate
     */
    AffiliateResponse execute();
}
