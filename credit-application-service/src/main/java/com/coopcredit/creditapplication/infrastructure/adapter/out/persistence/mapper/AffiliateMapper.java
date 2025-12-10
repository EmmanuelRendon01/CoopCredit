package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.AffiliateJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper to convert between Affiliate domain model and JPA entity.
 * 
 * MapStruct will automatically generate the implementation at compile time.
 * The generated class will be named AffiliateMapperImpl.
 * 
 * NOTE: creditApplications is ignored to prevent infinite recursion with
 * CreditApplicationMapper (Affiliate -> CreditApplications -> Affiliate -> ...)
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AffiliateMapper {

    /**
     * Converts JPA entity to domain model.
     * Ignores creditApplications to prevent circular reference with
     * CreditApplicationMapper.
     *
     * @param entity the JPA entity
     * @return domain model
     */
    @Mapping(target = "creditApplications", ignore = true)
    Affiliate toDomain(AffiliateJpaEntity entity);

    /**
     * Converts domain model to JPA entity.
     * Ignores creditApplications to prevent circular reference.
     *
     * @param domain the domain model
     * @return JPA entity
     */
    @Mapping(target = "creditApplications", ignore = true)
    AffiliateJpaEntity toEntity(Affiliate domain);

    /**
     * Updates existing entity with domain model data.
     * Only non-null values from domain will be copied.
     *
     * @param domain the domain model with updated data
     * @param entity the existing entity to update
     */
    @Mapping(target = "creditApplications", ignore = true)
    void updateEntity(Affiliate domain, @MappingTarget AffiliateJpaEntity entity);
}
