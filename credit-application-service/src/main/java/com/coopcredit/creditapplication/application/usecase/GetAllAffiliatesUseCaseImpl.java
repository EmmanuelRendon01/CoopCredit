package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.port.in.GetAllAffiliatesUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case implementation for retrieving all affiliates.
 */
@Service
@Transactional(readOnly = true)
public class GetAllAffiliatesUseCaseImpl implements GetAllAffiliatesUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetAllAffiliatesUseCaseImpl.class);

    private final AffiliateRepositoryPort affiliateRepository;

    public GetAllAffiliatesUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public List<AffiliateResponse> execute() {
        logger.info("Retrieving all affiliates");

        List<Affiliate> affiliates = affiliateRepository.findAll();

        logger.info("Found {} affiliates", affiliates.size());

        return affiliates.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
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
