package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.CreditApplicationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper to convert between CreditApplication domain model and JPA
 * entity.
 * 
 * Ignores riskEvaluation.creditApplication to avoid circular reference loops.
 */
@Mapper(componentModel = "spring", uses = {
        AffiliateMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CreditApplicationMapper {

    /**
     * Converts JPA entity to domain model.
     * Manually ignores riskEvaluation.creditApplication to break circular
     * dependency.
     *
     * @param entity the JPA entity
     * @return domain model
     */
    @Mapping(target = "riskEvaluation.creditApplication", ignore = true)
    CreditApplication toDomain(CreditApplicationJpaEntity entity);

    /**
     * Converts domain model to JPA entity.
     * Automatically maps nested affiliate.
     * Ignores riskEvaluation.creditApplication to prevent circular reference.
     *
     * @param domain the domain model
     * @return JPA entity
     */
    @Mapping(target = "riskEvaluation.creditApplication", ignore = true)
    CreditApplicationJpaEntity toEntity(CreditApplication domain);

    /**
     * Updates existing entity with domain model data.
     * Only non-null values from domain will be copied.
     *
     * @param domain the domain model with updated data
     * @param entity the existing entity to update
     */
    @Mapping(target = "riskEvaluation.creditApplication", ignore = true)
    void updateEntity(CreditApplication domain, @MappingTarget CreditApplicationJpaEntity entity);

    @org.mapstruct.AfterMapping
    default void setBidirectionalRelation(@MappingTarget CreditApplicationJpaEntity entity) {
        if (entity.getRiskEvaluation() != null) {
            entity.getRiskEvaluation().setCreditApplication(entity);
        }
    }
}
