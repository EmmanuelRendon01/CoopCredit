package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.creditapplication.domain.model.RiskEvaluation;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RiskEvaluationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper to convert between RiskEvaluation domain model and JPA entity.
 * 
 * Ignores creditApplication to avoid circular mapping with CreditApplicationMapper.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RiskEvaluationMapper {

    /**
     * Converts JPA entity to domain model.
     * Ignores creditApplication to avoid circular reference.
     *
     * @param entity the JPA entity
     * @return domain model
     */
    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluation toDomain(RiskEvaluationJpaEntity entity);

    /**
     * Converts domain model to JPA entity.
     * Ignores creditApplication - should be set separately.
     *
     * @param domain the domain model
     * @return JPA entity
     */
    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluationJpaEntity toEntity(RiskEvaluation domain);
}
