package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.CreditApplicationRequest;
import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapplication.application.port.in.RegisterCreditApplicationUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.application.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.domain.validation.BusinessValidator;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import com.coopcredit.creditapplication.infrastructure.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for registering a new credit application.
 * Validates business rules before creating the application.
 */
@Service
@Transactional
public class RegisterCreditApplicationUseCaseImpl implements RegisterCreditApplicationUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(RegisterCreditApplicationUseCaseImpl.class);
    
    private final CreditApplicationRepositoryPort applicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final MetricsService metricsService;
    
    public RegisterCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            MetricsService metricsService) {
        this.applicationRepository = applicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.metricsService = metricsService;
    }
    
    @Override
    public CreditApplicationResponse execute(Long affiliateId, CreditApplicationRequest request) {
        logger.info("Starting credit application registration for affiliate ID: {}", affiliateId);
        
        // 1. Validate affiliate exists
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
            .orElseThrow(() -> new ResourceNotFoundException("Affiliate", "id", affiliateId));
        
        logger.debug("Affiliate found: {} {} (Document: {})", 
            affiliate.getFirstName(), affiliate.getLastName(), affiliate.getDocumentNumber());
        
        // 2. Validate business rules
        validateBusinessRules(affiliate, request);
        
        // 3. Check for pending applications
        List<CreditApplication> pendingApplications = applicationRepository
            .findByAffiliateIdAndStatus(affiliateId, ApplicationStatus.PENDING);
        BusinessValidator.validateNoPendingApplications(!pendingApplications.isEmpty());
        
        // 4. Create credit application
        CreditApplication application = createCreditApplication(affiliate, request);
        
        // 5. Save application
        CreditApplication savedApplication = applicationRepository.save(application);
        
        // 6. Track metric
        metricsService.incrementApplicationCreated();
        
        logger.info("Credit application created successfully with ID: {} for affiliate: {}", 
            savedApplication.getId(), affiliateId);
        
        // 7. Map to response
        return mapToResponse(savedApplication, affiliate);
    }
    
    /**
     * Validates all business rules for credit application.
     */
    private void validateBusinessRules(Affiliate affiliate, CreditApplicationRequest request) {
        logger.debug("Validating business rules for affiliate ID: {}", affiliate.getId());
        
        // Validate affiliation time
        BusinessValidator.validateAffiliationTime(affiliate);
        
        // Validate credit amount
        BusinessValidator.validateCreditAmount(request.getRequestedAmount());
        
        // Validate credit term
        BusinessValidator.validateCreditTerm(request.getTermMonths());
        
        // Validate affiliate's credit limit based on salary
        BusinessValidator.validateAffiliateLimit(affiliate, request.getRequestedAmount());
        
        // Create temporary application to validate debt ratio
        CreditApplication tempApplication = new CreditApplication();
        tempApplication.setRequestedAmount(request.getRequestedAmount());
        tempApplication.setTermMonths(request.getTermMonths());
        tempApplication.setInterestRate(request.getInterestRate());
        tempApplication.setMonthlyIncome(request.getMonthlyIncome());
        tempApplication.setCurrentDebt(request.getCurrentDebt());
        
        BusinessValidator.validateDebtRatio(tempApplication);
        
        logger.debug("All business rules validated successfully");
    }
    
    /**
     * Creates a new CreditApplication entity from request.
     */
    private CreditApplication createCreditApplication(Affiliate affiliate, CreditApplicationRequest request) {
        CreditApplication application = new CreditApplication();
        application.setAffiliate(affiliate);
        application.setRequestedAmount(request.getRequestedAmount());
        application.setTermMonths(request.getTermMonths());
        application.setInterestRate(request.getInterestRate());
        application.setMonthlyIncome(request.getMonthlyIncome());
        application.setCurrentDebt(request.getCurrentDebt());
        application.setPurpose(request.getPurpose());
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationDate(LocalDateTime.now());
        
        return application;
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
        
        return response;
    }
}
