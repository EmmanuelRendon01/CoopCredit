package com.coopcredit.creditapplication.domain.port.out;

import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;

import java.util.List;
import java.util.Optional;

/**
 * Output Port: Credit Application Repository
 * Interface defining persistence operations for credit applications.
 */
public interface CreditApplicationRepositoryPort {
    
    CreditApplication save(CreditApplication application);
    
    Optional<CreditApplication> findById(Long id);
    
    List<CreditApplication> findByAffiliateId(Long affiliateId);
    
    List<CreditApplication> findByStatus(ApplicationStatus status);
    
    List<CreditApplication> findPendingApplications();
    
    List<CreditApplication> findAll();
    
    void delete(CreditApplication application);
}
