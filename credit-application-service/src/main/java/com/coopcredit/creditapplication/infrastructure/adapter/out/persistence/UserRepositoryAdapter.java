package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence;

import com.coopcredit.creditapplication.domain.model.Role;
import com.coopcredit.creditapplication.domain.model.RoleName;
import com.coopcredit.creditapplication.domain.model.User;
import com.coopcredit.creditapplication.domain.port.out.UserRepositoryPort;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA Adapter implementing UserRepositoryPort.
 * Translates domain operations to JPA repository calls.
 */
@Component
@Transactional
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userRepository, 
                                 RoleJpaRepository roleRepository,
                                 UserMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = userRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .map(this::roleToDomain);
    }

    /**
     * Converts Role JPA entity to domain model.
     *
     * @param entity the role entity
     * @return role domain model
     */
    private Role roleToDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());
        return role;
    }
}
