package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RiskEvaluationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Risk Evaluation entity.
 */
@Repository
public interface RiskEvaluationJpaRepository extends JpaRepository<RiskEvaluationJpaEntity, Long> {

    /**
     * Finds a risk evaluation by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return optional containing the evaluation if found
     */
    @Query("SELECT re FROM RiskEvaluationJpaEntity re WHERE re.creditApplication.id = :creditApplicationId")
    Optional<RiskEvaluationJpaEntity> findByCreditApplicationId(@Param("creditApplicationId") Long creditApplicationId);
}
