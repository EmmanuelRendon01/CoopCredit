package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.AffiliateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Affiliate entity.
 */
@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateJpaEntity, Long> {

    /**
     * Finds an affiliate by document number.
     *
     * @param documentNumber the document number to search for
     * @return optional containing the affiliate if found
     */
    Optional<AffiliateJpaEntity> findByDocumentNumber(String documentNumber);

    /**
     * Checks if an affiliate exists with the given document number.
     *
     * @param documentNumber the document number to check
     * @return true if exists, false otherwise
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Checks if an affiliate exists with the given email.
     *
     * @param email the email address to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if an affiliate exists with the given email, excluding a specific ID.
     *
     * @param email the email address to check
     * @param id the affiliate ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Finds an affiliate by associated user username.
     *
     * @param username the username to search for
     * @return optional containing the affiliate if found
     */
    @Query("SELECT a FROM AffiliateJpaEntity a WHERE a.user.username = :username")
    Optional<AffiliateJpaEntity> findByUsername(@Param("username") String username);
}
