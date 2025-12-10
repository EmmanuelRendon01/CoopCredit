package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;

/**
 * Port IN - Use case for evaluating credit applications.
 */
public interface EvaluateCreditApplicationUseCase {
    CreditApplicationResponse execute(Long applicationId);
}
