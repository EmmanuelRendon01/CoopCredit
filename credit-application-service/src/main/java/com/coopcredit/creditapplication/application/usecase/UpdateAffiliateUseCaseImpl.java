package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.dto.UpdateAffiliateRequest;
import com.coopcredit.creditapplication.application.port.in.UpdateAffiliateUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.infrastructure.exception.BusinessException;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for updating affiliate information.
 * 
 * Applies business rules:
 * - Email uniqueness validation
 * - Cannot update if email already exists for another affiliate
 */
@Service
@Transactional
public class UpdateAffiliateUseCaseImpl implements UpdateAffiliateUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UpdateAffiliateUseCaseImpl.class);

    private final AffiliateRepositoryPort affiliateRepository;

    public UpdateAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public AffiliateResponse execute(Long affiliateId, UpdateAffiliateRequest request) {
        logger.info("Updating affiliate with ID: {}", affiliateId);

        // Find existing affiliate
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
            .orElseThrow(() -> new ResourceNotFoundException("Affiliate", "id", affiliateId));

        // Validate email uniqueness (if changed)
        if (!affiliate.getEmail().equals(request.getEmail())) {
            if (affiliateRepository.existsByEmailAndIdNot(request.getEmail(), affiliateId)) {
                throw new BusinessException(
                    "EMAIL_ALREADY_EXISTS",
                    "An affiliate with email '" + request.getEmail() + "' already exists"
                );
            }
        }

        // Update editable fields
        affiliate.setFirstName(request.getFirstName());
        affiliate.setLastName(request.getLastName());
        affiliate.setEmail(request.getEmail());
        affiliate.setPhone(request.getPhone());
        affiliate.setSalary(request.getSalary());
        affiliate.setStatus(request.getStatus());

        // Save updated affiliate
        Affiliate updated = affiliateRepository.save(affiliate);

        logger.info("Successfully updated affiliate ID: {}", affiliateId);

        // Map to response
        return mapToResponse(updated);
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
