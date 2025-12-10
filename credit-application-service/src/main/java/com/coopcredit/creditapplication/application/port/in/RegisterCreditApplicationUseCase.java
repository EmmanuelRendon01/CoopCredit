package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.CreditApplicationRequest;
import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;

/**
 * Port IN - Use case for registering credit applications.
 */
public interface RegisterCreditApplicationUseCase {
    CreditApplicationResponse execute(Long affiliateId, CreditApplicationRequest request);
}
