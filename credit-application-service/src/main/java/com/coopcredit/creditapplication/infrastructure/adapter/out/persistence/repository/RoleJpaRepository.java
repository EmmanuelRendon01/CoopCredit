package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.creditapplication.domain.model.RoleName;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Role entity.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    /**
     * Finds a role by its name.
     *
     * @param name the role name
     * @return optional containing the role if found
     */
    Optional<RoleJpaEntity> findByName(RoleName name);
}
