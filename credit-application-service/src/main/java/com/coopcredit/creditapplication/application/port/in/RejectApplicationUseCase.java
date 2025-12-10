package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;

/**
 * Use case for manually rejecting a credit application.
 * Allows analysts to override automatic evaluation and reject applications.
 */
public interface RejectApplicationUseCase {
    
    /**
     * Rejects a credit application manually.
     * 
     * @param applicationId the application ID to reject
     * @return the rejected application
     */
    CreditApplicationResponse execute(Long applicationId);
}
