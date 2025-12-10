package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapplication.application.port.in.RejectApplicationUseCase;
import com.coopcredit.creditapplication.application.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import com.coopcredit.creditapplication.infrastructure.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for manually rejecting credit applications.
 */
@Service
@Transactional
public class RejectApplicationUseCaseImpl implements RejectApplicationUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(RejectApplicationUseCaseImpl.class);
    
    private final CreditApplicationRepositoryPort applicationRepository;
    private final MetricsService metricsService;
    
    public RejectApplicationUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            MetricsService metricsService) {
        this.applicationRepository = applicationRepository;
        this.metricsService = metricsService;
    }
    
    @Override
    public CreditApplicationResponse execute(Long applicationId) {
        logger.info("Manually rejecting credit application ID: {}", applicationId);
        
        // 1. Validate application exists
        CreditApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("CreditApplication", "id", applicationId));
        
        // 2. Reject application
        application.reject();
        
        // 3. Save updated application
        CreditApplication savedApplication = applicationRepository.save(application);
        
        // 4. Track metrics
        metricsService.incrementApplicationRejected();
        
        logger.info("Credit application {} manually rejected", applicationId);
        
        // 5. Map to response
        return mapToResponse(savedApplication);
    }
    
    private CreditApplicationResponse mapToResponse(CreditApplication application) {
        CreditApplicationResponse response = new CreditApplicationResponse();
        response.setId(application.getId());
        response.setAffiliateId(application.getAffiliate().getId());
        response.setAffiliateName(
            application.getAffiliate().getFirstName() + " " + application.getAffiliate().getLastName()
        );
        response.setRequestedAmount(application.getRequestedAmount());
        response.setTermMonths(application.getTermMonths());
        response.setInterestRate(application.getInterestRate());
        response.setMonthlyPayment(application.calculateMonthlyPayment(application.getInterestRate()));
        response.setStatus(application.getStatus().name());
        response.setPurpose(application.getPurpose());
        response.setApplicationDate(application.getApplicationDate());
        response.setEvaluationDate(application.getEvaluationDate());
        response.setEvaluationComments(application.getEvaluationComments());
        
        if (application.getRiskEvaluation() != null) {
            response.setCreditScore(application.getRiskEvaluation().getCreditScore());
            response.setRiskLevel(application.getRiskEvaluation().getRiskLevel());
        }
        
        return response;
    }
}
