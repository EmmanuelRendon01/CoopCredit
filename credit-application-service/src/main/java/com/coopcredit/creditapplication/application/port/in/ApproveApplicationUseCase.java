package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;

/**
 * Use case for manually approving a credit application.
 * Allows analysts to override automatic evaluation and approve applications.
 */
public interface ApproveApplicationUseCase {
    
    /**
     * Approves a credit application manually.
     * 
     * @param applicationId the application ID to approve
     * @return the approved application
     */
    CreditApplicationResponse execute(Long applicationId);
}
