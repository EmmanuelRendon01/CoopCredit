package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence;

import com.coopcredit.creditapplication.domain.model.Affiliate;
import com.coopcredit.creditapplication.application.port.out.AffiliateRepositoryPort;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.AffiliateJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper.AffiliateMapper;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.AffiliateJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA Adapter implementing AffiliateRepositoryPort.
 * Translates domain operations to JPA repository calls.
 */
@Component
@Transactional
public class AffiliateRepositoryAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository repository;
    private final AffiliateMapper mapper;

    public AffiliateRepositoryAdapter(AffiliateJpaRepository repository, AffiliateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateJpaEntity entity;
        
        if (affiliate.getId() != null) {
            // Update existing
            entity = repository.findById(affiliate.getId())
                .orElseThrow(() -> new RuntimeException("Affiliate not found with id: " + affiliate.getId()));
            mapper.updateEntity(affiliate, entity);
        } else {
            // Create new
            entity = mapper.toEntity(affiliate);
        }
        
        AffiliateJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Affiliate> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumentNumber(String documentNumber) {
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailAndIdNot(String email, Long excludeId) {
        return repository.existsByEmailAndIdNot(email, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Affiliate> findAll() {
        return repository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    // Additional methods not in port interface but useful for infrastructure
    @Transactional(readOnly = true)
    public Optional<Affiliate> findByUsername(String username) {
        return repository.findByUsername(username)
            .map(mapper::toDomain);
    }

    public void delete(Affiliate affiliate) {
        if (affiliate.getId() != null) {
            repository.deleteById(affiliate.getId());
        }
    }
}
