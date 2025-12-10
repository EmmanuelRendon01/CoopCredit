package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.creditapplication.domain.model.Role;
import com.coopcredit.creditapplication.domain.model.User;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper to convert between User/Role domain models and JPA entities.
 * 
 * Uses AffiliateMapper for nested affiliate mapping.
 */
@Mapper(
    componentModel = "spring",
    uses = {AffiliateMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Converts JPA entity to domain model.
     * Automatically maps nested roles and affiliate.
     *
     * @param entity the JPA entity
     * @return domain model
     */
    User toDomain(UserJpaEntity entity);

    /**
     * Converts domain model to JPA entity.
     * Automatically maps nested roles.
     *
     * @param domain the domain model
     * @return JPA entity
     */
    UserJpaEntity toEntity(User domain);

    /**
     * Converts Role JPA entity to domain model.
     *
     * @param entity the role entity
     * @return role domain model
     */
    Role roleToDomain(RoleJpaEntity entity);

    /**
     * Converts Role domain model to JPA entity.
     *
     * @param domain the role domain model
     * @return role entity
     */
    RoleJpaEntity roleToEntity(Role domain);
}
