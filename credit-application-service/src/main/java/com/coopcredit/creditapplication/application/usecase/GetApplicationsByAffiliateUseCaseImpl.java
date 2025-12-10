package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapplication.application.port.in.GetApplicationsByAffiliateUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.application.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for retrieving all credit applications for a specific affiliate.
 */
@Service
@Transactional(readOnly = true)
public class GetApplicationsByAffiliateUseCaseImpl implements GetApplicationsByAffiliateUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetApplicationsByAffiliateUseCaseImpl.class);
    
    private final CreditApplicationRepositoryPort applicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    
    public GetApplicationsByAffiliateUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        this.applicationRepository = applicationRepository;
        this.affiliateRepository = affiliateRepository;
    }
    
    @Override
    public List<CreditApplicationResponse> execute(Long affiliateId) {
        logger.info("Retrieving credit applications for affiliate ID: {}", affiliateId);
        
        // Validate affiliate exists
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
            .orElseThrow(() -> new ResourceNotFoundException("Affiliate", "id", affiliateId));
        
        // Get all applications for this affiliate
        List<CreditApplication> applications = applicationRepository.findByAffiliateId(affiliateId);
        
        logger.debug("Found {} applications for affiliate: {} {}", 
            applications.size(), affiliate.getFirstName(), affiliate.getLastName());
        
        // Map to response DTOs
        return applications.stream()
            .map(app -> mapToResponse(app, affiliate))
            .collect(Collectors.toList());
    }
    
    /**
     * Maps CreditApplication entity to response DTO.
     */
    private CreditApplicationResponse mapToResponse(CreditApplication application, Affiliate affiliate) {
        CreditApplicationResponse response = new CreditApplicationResponse();
        response.setId(application.getId());
        response.setAffiliateId(affiliate.getId());
        response.setAffiliateName(affiliate.getFirstName() + " " + affiliate.getLastName());
        response.setRequestedAmount(application.getRequestedAmount());
        response.setTermMonths(application.getTermMonths());
        response.setInterestRate(application.getInterestRate());
        response.setMonthlyPayment(application.calculateMonthlyPayment(application.getInterestRate()));
        response.setStatus(application.getStatus().name());
        response.setPurpose(application.getPurpose());
        response.setApplicationDate(application.getApplicationDate());
        response.setEvaluationDate(application.getEvaluationDate());
        response.setEvaluationComments(application.getEvaluationComments());
        
        // Extract credit score from comments if available
        if (application.getEvaluationComments() != null && application.getEvaluationComments().contains("Credit Score:")) {
            try {
                String scoreStr = application.getEvaluationComments()
                    .split("Credit Score: ")[1]
                    .split(" \\|")[0]
                    .trim();
                response.setCreditScore(Integer.parseInt(scoreStr));
            } catch (Exception e) {
                logger.warn("Failed to extract credit score from comments for application {}", application.getId());
            }
        }
        
        // Extract risk level
        if (application.getEvaluationComments() != null && application.getEvaluationComments().contains("Risk Level:")) {
            try {
                String riskLevel = application.getEvaluationComments()
                    .split("Risk Level: ")[1]
                    .split(" \\|")[0]
                    .trim();
                response.setRiskLevel(riskLevel);
            } catch (Exception e) {
                logger.warn("Failed to extract risk level from comments for application {}", application.getId());
            }
        }
        
        return response;
    }
}
