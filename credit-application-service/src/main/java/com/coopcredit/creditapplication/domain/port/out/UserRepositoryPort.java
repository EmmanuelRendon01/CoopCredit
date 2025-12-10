package com.coopcredit.creditapplication.domain.port.out;

import com.coopcredit.creditapplication.domain.model.Role;
import com.coopcredit.creditapplication.domain.model.RoleName;
import com.coopcredit.creditapplication.domain.model.User;

import java.util.Optional;

/**
 * Output Port: User Repository
 * Interface defining persistence operations for users and roles.
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Optional<Role> findRoleByName(RoleName roleName);
}
