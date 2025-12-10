package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;

import java.util.List;

/**
 * Use case interface for retrieving all affiliates.
 * 
 * Note: For production systems, this should support pagination.
 */
public interface GetAllAffiliatesUseCase {
    
    /**
     * Retrieves all affiliates in the system.
     *
     * @return list of all affiliates
     */
    List<AffiliateResponse> execute();
}
