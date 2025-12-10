package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.port.in.GetAffiliateByIdUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for retrieving a single affiliate by ID.
 */
@Service
@Transactional(readOnly = true)
public class GetAffiliateByIdUseCaseImpl implements GetAffiliateByIdUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetAffiliateByIdUseCaseImpl.class);

    private final AffiliateRepositoryPort affiliateRepository;

    public GetAffiliateByIdUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public AffiliateResponse execute(Long affiliateId) {
        logger.info("Retrieving affiliate with ID: {}", affiliateId);

        Affiliate affiliate = affiliateRepository.findById(affiliateId)
            .orElseThrow(() -> new ResourceNotFoundException("Affiliate", "id", affiliateId));

        return mapToResponse(affiliate);
    }

    /**
     * Maps domain affiliate to response DTO.
     *
     * @param affiliate the domain affiliate
     * @return affiliate response DTO
     */
    private AffiliateResponse mapToResponse(Affiliate affiliate) {
        AffiliateResponse response = new AffiliateResponse();
        response.setId(affiliate.getId());
        response.setDocumentType(affiliate.getDocumentType());
        response.setDocumentNumber(affiliate.getDocumentNumber());
        response.setFirstName(affiliate.getFirstName());
        response.setLastName(affiliate.getLastName());
        response.setEmail(affiliate.getEmail());
        response.setPhone(affiliate.getPhone());
        response.setSalary(affiliate.getSalary());
        response.setAffiliationDate(affiliate.getAffiliationDate());
        response.setStatus(affiliate.getStatus());
        response.setMonthsAsAffiliate(affiliate.getMonthsAsAffiliate());
        response.setTotalCreditApplications(
            affiliate.getCreditApplications() != null ? affiliate.getCreditApplications().size() : 0
        );
        return response;
    }
}
