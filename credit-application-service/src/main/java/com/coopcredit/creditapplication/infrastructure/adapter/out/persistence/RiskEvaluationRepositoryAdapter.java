package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence;

import com.coopcredit.creditapplication.domain.model.RiskEvaluation;
import com.coopcredit.creditapplication.domain.port.out.RiskEvaluationRepositoryPort;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RiskEvaluationJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper.RiskEvaluationMapper;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA Adapter implementing RiskEvaluationRepositoryPort.
 * Translates domain operations to JPA repository calls.
 */
@Component
@Transactional
public class RiskEvaluationRepositoryAdapter implements RiskEvaluationRepositoryPort {

    private final RiskEvaluationJpaRepository repository;
    private final RiskEvaluationMapper mapper;

    public RiskEvaluationRepositoryAdapter(RiskEvaluationJpaRepository repository, 
                                           RiskEvaluationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RiskEvaluation save(RiskEvaluation evaluation) {
        RiskEvaluationJpaEntity entity = mapper.toEntity(evaluation);
        RiskEvaluationJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RiskEvaluation> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RiskEvaluation> findByCreditApplicationId(Long applicationId) {
        return repository.findByCreditApplicationId(applicationId)
            .map(mapper::toDomain);
    }
}
