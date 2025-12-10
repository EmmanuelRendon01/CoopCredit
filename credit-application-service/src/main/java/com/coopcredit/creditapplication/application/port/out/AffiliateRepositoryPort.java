package com.coopcredit.creditapplication.application.port.out;

import com.coopcredit.creditapplication.domain.model.Affiliate;

import java.util.List;
import java.util.Optional;

/**
 * Port OUT - Repository for affiliates.
 * Defines persistence operations without depending on implementation.
 */
public interface AffiliateRepositoryPort {
    
    /**
     * Saves an affiliate (create or update).
     *
     * @param affiliate the affiliate to save
     * @return saved affiliate
     */
    Affiliate save(Affiliate affiliate);
    
    /**
     * Finds an affiliate by ID.
     *
     * @param id the affiliate ID
     * @return optional containing affiliate if found
     */
    Optional<Affiliate> findById(Long id);
    
    /**
     * Finds an affiliate by document number.
     *
     * @param documentNumber the document number
     * @return optional containing affiliate if found
     */
    Optional<Affiliate> findByDocumentNumber(String documentNumber);
    
    /**
     * Finds an affiliate by username (email).
     *
     * @param username the username (email)
     * @return optional containing affiliate if found
     */
    Optional<Affiliate> findByUsername(String username);
    
    /**
     * Checks if an affiliate exists with the given document number.
     *
     * @param documentNumber the document number
     * @return true if exists, false otherwise
     */
    boolean existsByDocumentNumber(String documentNumber);
    
    /**
     * Checks if an affiliate exists with the given email.
     *
     * @param email the email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if an affiliate exists with the given email, excluding a specific ID.
     *
     * @param email the email address
     * @param excludeId the affiliate ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long excludeId);
    
    /**
     * Retrieves all affiliates.
     *
     * @return list of all affiliates
     */
    List<Affiliate> findAll();
}
