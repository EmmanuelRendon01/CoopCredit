package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * Finds a user by username with roles loaded.
     *
     * @param username the username to search for
     * @return optional containing the user if found
     */
    @EntityGraph(value = "User.withRoles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<UserJpaEntity> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return optional containing the user if found
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
