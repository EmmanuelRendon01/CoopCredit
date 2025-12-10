package com.coopcredit.creditapplication.domain.port.out;

import com.coopcredit.creditapplication.domain.model.Affiliate;

import java.util.Optional;

/**
 * Output Port: Affiliate Repository
 * Interface defining persistence operations without depending on implementation.
 * Follows the Dependency Inversion Principle of hexagonal architecture.
 */
public interface AffiliateRepositoryPort {
    
    Affiliate save(Affiliate affiliate);
    
    Optional<Affiliate> findById(Long id);
    
    Optional<Affiliate> findByDocumentNumber(String documentNumber);
    
    Optional<Affiliate> findByUsername(String username);
    
    boolean existsByDocumentNumber(String documentNumber);
    
    void delete(Affiliate affiliate);
}
