package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence;

import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import com.coopcredit.creditapplication.domain.model.CreditApplication;
import com.coopcredit.creditapplication.application.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.CreditApplicationJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper.CreditApplicationMapper;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.CreditApplicationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA Adapter implementing CreditApplicationRepositoryPort.
 * Translates domain operations to JPA repository calls.
 */
@Component
@Transactional
public class CreditApplicationRepositoryAdapter implements CreditApplicationRepositoryPort {

    private final CreditApplicationJpaRepository repository;
    private final CreditApplicationMapper mapper;

    public CreditApplicationRepositoryAdapter(CreditApplicationJpaRepository repository,
            CreditApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CreditApplication save(CreditApplication application) {
        CreditApplicationJpaEntity entity;

        if (application.getId() != null) {
            // Update existing
            entity = repository.findById(application.getId())
                    .orElseThrow(
                            () -> new RuntimeException("Credit application not found with id: " + application.getId()));
            mapper.updateEntity(application, entity);
        } else {
            // Create new
            entity = mapper.toEntity(application);
        }

        CreditApplicationJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CreditApplication> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return repository.findByAffiliateId(affiliateId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findByAffiliateIdAndStatus(Long affiliateId, ApplicationStatus status) {
        return repository.findByAffiliateIdAndStatus(affiliateId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    // Additional methods not in port interface but useful for infrastructure
    @Transactional(readOnly = true)
    public List<CreditApplication> findByStatus(ApplicationStatus status) {
        return repository.findByStatusOrderByApplicationDateDesc(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CreditApplication> findPendingApplications() {
        return repository.findPendingApplications().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CreditApplication> findAll() {
        return repository.findAllWithDetails().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public void delete(CreditApplication application) {
        if (application.getId() != null) {
            repository.deleteById(application.getId());
        }
    }
}
