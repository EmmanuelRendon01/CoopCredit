package com.coopcredit.creditapplication.application.port.in;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;

import java.util.List;

/**
 * Port IN - Use case for getting applications by affiliate.
 */
public interface GetApplicationsByAffiliateUseCase {
    List<CreditApplicationResponse> execute(Long affiliateId);
}
