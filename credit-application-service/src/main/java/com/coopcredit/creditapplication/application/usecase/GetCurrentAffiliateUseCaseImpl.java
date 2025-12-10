package com.coopcredit.creditapplication.application.usecase;

import com.coopcredit.creditapplication.application.dto.AffiliateResponse;
import com.coopcredit.creditapplication.application.port.in.GetCurrentAffiliateUseCase;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for retrieving the current authenticated affiliate.
 */
@Service
@Transactional(readOnly = true)
public class GetCurrentAffiliateUseCaseImpl implements GetCurrentAffiliateUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetCurrentAffiliateUseCaseImpl.class);
    
    private final AffiliateRepositoryPort affiliateRepository;
    
    public GetCurrentAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }
    
    @Override
    public AffiliateResponse execute() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Getting current affiliate for user: {}", username);
        
        // Find affiliate by username (email)
        Affiliate affiliate = affiliateRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Affiliate", "username", username));
        
        return mapToResponse(affiliate);
    }
    
    private AffiliateResponse mapToResponse(Affiliate affiliate) {
        AffiliateResponse response = new AffiliateResponse();
        response.setId(affiliate.getId());
        response.setDocumentType(affiliate.getDocumentType());
        response.setDocumentNumber(affiliate.getDocumentNumber());
        response.setFirstName(affiliate.getFirstName());
        response.setLastName(affiliate.getLastName());
        response.setEmail(affiliate.getEmail());
        response.setPhone(affiliate.getPhone());
        response.setSalary(affiliate.getSalary());
        response.setStatus(affiliate.getStatus());
        response.setAffiliationDate(affiliate.getAffiliationDate());
        return response;
    }
}
