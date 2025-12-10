package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.CreditApplicationJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Credit Application entity.
 */
@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationJpaEntity, Long> {

    /**
     * Finds a credit application by ID with affiliate loaded.
     *
     * @param id the application ID
     * @return optional containing the application if found
     */
    @EntityGraph(value = "CreditApplication.withAffiliate", type = EntityGraph.EntityGraphType.LOAD)
    Optional<CreditApplicationJpaEntity> findById(Long id);

    /**
     * Finds all credit applications for a specific affiliate.
     *
     * @param affiliateId the affiliate ID
     * @return list of credit applications
     */
    @EntityGraph(value = "CreditApplication.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT ca FROM CreditApplicationJpaEntity ca WHERE ca.affiliate.id = :affiliateId ORDER BY ca.applicationDate DESC")
    List<CreditApplicationJpaEntity> findByAffiliateId(@Param("affiliateId") Long affiliateId);

    /**
     * Finds all credit applications for a specific affiliate with a specific status.
     *
     * @param affiliateId the affiliate ID
     * @param status the application status
     * @return list of credit applications
     */
    @EntityGraph(value = "CreditApplication.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT ca FROM CreditApplicationJpaEntity ca WHERE ca.affiliate.id = :affiliateId AND ca.status = :status ORDER BY ca.applicationDate DESC")
    List<CreditApplicationJpaEntity> findByAffiliateIdAndStatus(@Param("affiliateId") Long affiliateId, @Param("status") ApplicationStatus status);

    /**
     * Finds all credit applications with a specific status.
     *
     * @param status the application status
     * @return list of credit applications
     */
    @EntityGraph(value = "CreditApplication.withAffiliate", type = EntityGraph.EntityGraphType.LOAD)
    List<CreditApplicationJpaEntity> findByStatusOrderByApplicationDateDesc(ApplicationStatus status);

    /**
     * Finds all pending credit applications (optimized with join fetch).
     *
     * @return list of pending applications
     */
    @Query("SELECT ca FROM CreditApplicationJpaEntity ca " +
           "JOIN FETCH ca.affiliate " +
           "WHERE ca.status = 'PENDING' " +
           "ORDER BY ca.applicationDate ASC")
    List<CreditApplicationJpaEntity> findPendingApplications();

    /**
     * Finds all applications with full details loaded.
     *
     * @return list of all applications
     */
    @EntityGraph(value = "CreditApplication.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT ca FROM CreditApplicationJpaEntity ca ORDER BY ca.applicationDate DESC")
    List<CreditApplicationJpaEntity> findAllWithDetails();
}
