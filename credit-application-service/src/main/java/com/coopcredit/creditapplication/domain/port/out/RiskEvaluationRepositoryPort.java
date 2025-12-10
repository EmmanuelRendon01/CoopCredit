package com.coopcredit.creditapplication.domain.port.out;

import com.coopcredit.creditapplication.domain.model.RiskEvaluation;

import java.util.Optional;

/**
 * Output Port: Risk Evaluation Repository
 * Interface defining persistence operations for risk evaluations.
 */
public interface RiskEvaluationRepositoryPort {
    
    RiskEvaluation save(RiskEvaluation evaluation);
    
    Optional<RiskEvaluation> findById(Long id);
    
    Optional<RiskEvaluation> findByCreditApplicationId(Long applicationId);
}
