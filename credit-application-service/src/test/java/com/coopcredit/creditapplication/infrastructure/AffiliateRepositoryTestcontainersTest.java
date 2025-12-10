package com.coopcredit.creditapplication.infrastructure;

import com.coopcredit.creditapplication.domain.model.AffiliateStatus;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.AffiliateJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.AffiliateJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests using Testcontainers with PostgreSQL.
 * Validates real database operations with containerized PostgreSQL.
 * 
 * NOTE: Disabled by default because requires Docker daemon access without sudo.
 * To enable: Remove @Disabled annotation and ensure DOCKER_HOST is properly configured.
 */
@Disabled("Requires Docker daemon access - enable manually if needed")
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("AffiliateRepository - Testcontainers Integration Tests")
class AffiliateRepositoryTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AffiliateJpaRepository affiliateRepository;

    @Test
    @DisplayName("PostgreSQL container should be running")
    void containerShouldBeRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should save and retrieve affiliate from real database")
    void shouldSaveAndRetrieveAffiliate() {
        // Arrange
        AffiliateJpaEntity affiliate = new AffiliateJpaEntity();
        affiliate.setDocumentType("CC");
        affiliate.setDocumentNumber("1234567890");
        affiliate.setFirstName("Juan");
        affiliate.setLastName("Pérez");
        affiliate.setEmail("juan.perez@example.com");
        affiliate.setPhone("3001234567");
        affiliate.setSalary(new BigDecimal("3000000"));
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(6));
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        // Act
        AffiliateJpaEntity saved = affiliateRepository.save(affiliate);

        // Assert
        assertThat(saved.getId()).isNotNull();
        AffiliateJpaEntity retrieved = affiliateRepository.findById(saved.getId()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getDocumentNumber()).isEqualTo("1234567890");
        assertThat(retrieved.getFirstName()).isEqualTo("Juan");
        assertThat(retrieved.getStatus()).isEqualTo(AffiliateStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should enforce unique document number constraint")
    void shouldEnforceUniqueDocumentNumber() {
        // Arrange
        AffiliateJpaEntity affiliate1 = new AffiliateJpaEntity();
        affiliate1.setDocumentType("CC");
        affiliate1.setDocumentNumber("9876543210");
        affiliate1.setFirstName("María");
        affiliate1.setLastName("García");
        affiliate1.setEmail("maria@example.com");
        affiliate1.setPhone("3009876543");
        affiliate1.setSalary(new BigDecimal("2500000"));
        affiliate1.setAffiliationDate(LocalDate.now());
        affiliate1.setStatus(AffiliateStatus.ACTIVE);

        affiliateRepository.save(affiliate1);

        AffiliateJpaEntity affiliate2 = new AffiliateJpaEntity();
        affiliate2.setDocumentType("CC");
        affiliate2.setDocumentNumber("9876543210"); // Same document number
        affiliate2.setFirstName("Pedro");
        affiliate2.setLastName("López");
        affiliate2.setEmail("pedro@example.com");
        affiliate2.setPhone("3001111111");
        affiliate2.setSalary(new BigDecimal("2800000"));
        affiliate2.setAffiliationDate(LocalDate.now());
        affiliate2.setStatus(AffiliateStatus.ACTIVE);

        // Act & Assert
        assertThatThrownBy(() -> {
            affiliateRepository.save(affiliate2);
            affiliateRepository.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle transactional operations correctly")
    void shouldHandleTransactionalOperations() {
        // Arrange
        AffiliateJpaEntity affiliate = new AffiliateJpaEntity();
        affiliate.setDocumentType("CE");
        affiliate.setDocumentNumber("5555555555");
        affiliate.setFirstName("Carlos");
        affiliate.setLastName("Rodríguez");
        affiliate.setEmail("carlos@example.com");
        affiliate.setPhone("3005555555");
        affiliate.setSalary(new BigDecimal("4000000"));
        affiliate.setAffiliationDate(LocalDate.now().minusYears(1));
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        // Act
        AffiliateJpaEntity saved = affiliateRepository.save(affiliate);
        saved.setSalary(new BigDecimal("4500000"));
        AffiliateJpaEntity updated = affiliateRepository.save(saved);

        // Assert
        assertThat(updated.getSalary()).isEqualByComparingTo(new BigDecimal("4500000"));
        AffiliateJpaEntity retrieved = affiliateRepository.findById(updated.getId()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getSalary()).isEqualByComparingTo(new BigDecimal("4500000"));
    }
}
