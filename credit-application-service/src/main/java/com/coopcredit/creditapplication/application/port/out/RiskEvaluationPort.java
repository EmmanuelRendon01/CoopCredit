package com.coopcredit.creditapplication.application.port.out;

import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.domain.model.RiskEvaluation;

/**
 * Port OUT - External service for risk evaluation.
 */
public interface RiskEvaluationPort {
    RiskEvaluation evaluateRisk(CreditApplication application);
}
