package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapplication.application.port.in.EvaluateCreditApplicationUseCase;
import com.coopcredit.creditapplication.application.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.creditapplication.application.port.out.RiskEvaluationPort;
import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.domain.model.RiskEvaluation;
import com.coopcredit.creditapplication.domain.validation.BusinessValidator;
import com.coopcredit.creditapplication.infrastructure.exception.BusinessException;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import com.coopcredit.creditapplication.infrastructure.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use case for evaluating a credit application using external risk service.
 * Updates application status based on credit score.
 */
@Service
@Transactional
public class EvaluateCreditApplicationUseCaseImpl implements EvaluateCreditApplicationUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(EvaluateCreditApplicationUseCaseImpl.class);
    
    private final CreditApplicationRepositoryPort applicationRepository;
    private final RiskEvaluationPort riskEvaluationPort;
    private final MetricsService metricsService;
    
    public EvaluateCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            RiskEvaluationPort riskEvaluationPort,
            MetricsService metricsService) {
        this.applicationRepository = applicationRepository;
        this.riskEvaluationPort = riskEvaluationPort;
        this.metricsService = metricsService;
    }
    
    @Override
    public CreditApplicationResponse execute(Long applicationId) {
        logger.info("Starting evaluation for credit application ID: {}", applicationId);
        
        // 1. Validate application exists
        CreditApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("CreditApplication", "id", applicationId));
        
        // 2. Validate application is in PENDING status
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(
                "INVALID_APPLICATION_STATUS",
                String.format("Application %d is not pending evaluation. Current status: %s", 
                    applicationId, application.getStatus())
            );
        }
        
        logger.debug("Evaluating application for affiliate: {} (Amount: {}, Term: {} months)",
            application.getAffiliate().getDocumentNumber(),
            application.getRequestedAmount(),
            application.getTermMonths());
        
        // 3. Call external risk evaluation service
        RiskEvaluation riskEvaluation = evaluateRisk(application);
        
        // 4. Update application status based on score
        updateApplicationStatus(application, riskEvaluation);
        
        // 5. Save updated application
        CreditApplication savedApplication = applicationRepository.save(application);
        
        // 7. Track metrics
        if (savedApplication.getStatus() == ApplicationStatus.APPROVED) {
            metricsService.incrementApplicationApproved();
        } else if (savedApplication.getStatus() == ApplicationStatus.REJECTED) {
            metricsService.incrementApplicationRejected();
        }
        
        logger.info("Credit application {} evaluated. Status: {}, Score: {}", 
            applicationId, savedApplication.getStatus(), riskEvaluation.getCreditScore());
        
        // 7. Map to response
        return mapToResponse(savedApplication);
    }
    
    /**
     * Calls external risk evaluation service.
     */
    private RiskEvaluation evaluateRisk(CreditApplication application) {
        try {
            logger.debug("Calling external risk evaluation service");
            RiskEvaluation evaluation = riskEvaluationPort.evaluateRisk(application);
            
            logger.debug("Risk evaluation received - Score: {}, Level: {}, Recommendation: {}", 
                evaluation.getCreditScore(), 
                evaluation.getRiskLevel(), 
                evaluation.getRecommendation());
            
            return evaluation;
        } catch (Exception e) {
            logger.error("Error calling risk evaluation service", e);
            throw new BusinessException(
                "RISK_SERVICE_ERROR",
                "Failed to evaluate credit risk: " + e.getMessage()
            );
        }
    }
    
    /**
     * Updates application status based on risk evaluation score.
     */
    private void updateApplicationStatus(CreditApplication application, RiskEvaluation riskEvaluation) {
        application.setEvaluationDate(LocalDateTime.now());
        
        // Set risk evaluation which automatically updates the status
        application.setRiskEvaluation(riskEvaluation);
        
        // Add evaluation comments with risk details
        String comments = String.format(
            "Credit Score: %d | Risk Level: %s | Recommendation: %s | Factors: %s",
            riskEvaluation.getCreditScore(),
            riskEvaluation.getRiskLevel(),
            riskEvaluation.getRecommendation(),
            riskEvaluation.getRiskFactors() != null ? String.join(", ", riskEvaluation.getRiskFactors()) : "None"
        );
        application.setEvaluationComments(comments);
        
        logger.debug("Application status updated to: {}", application.getStatus());
    }
    
    /**
     * Maps CreditApplication entity to response DTO.
     */
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
        
        // Extract credit score from comments if available
        if (application.getEvaluationComments() != null && application.getEvaluationComments().contains("Credit Score:")) {
            try {
                String scoreStr = application.getEvaluationComments()
                    .split("Credit Score: ")[1]
                    .split(" \\|")[0]
                    .trim();
                response.setCreditScore(Integer.parseInt(scoreStr));
            } catch (Exception e) {
                logger.warn("Failed to extract credit score from comments", e);
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
                logger.warn("Failed to extract risk level from comments", e);
            }
        }
        
        return response;
    }
}
