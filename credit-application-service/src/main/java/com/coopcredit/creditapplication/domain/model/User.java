package com.coopcredit.creditapplication.domain.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity: User (Security)
 * Pure POJO without infrastructure dependencies.
 * Represents a system user with authentication and authorization data.
 * 
 * Business Rules:
 * - Username must be unique
 * - Password is stored encrypted (BCrypt)
 * - A user can have multiple roles
 * - Only enabled users can authenticate
 */
public class User {
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Set<Role> roles;
    private Affiliate affiliate; // Relación con afiliado si aplica

    public User() {
        this.roles = new HashSet<>();
        this.enabled = true;
    }

    public User(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Business Logic
    
    /**
     * Adds a role to this user.
     * 
     * @param role the role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Checks if the user has a specific role.
     * 
     * @param roleName the role name to check
     * @return true if user has the role
     */
    public boolean hasRole(RoleName roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName() == roleName);
    }

    /**
     * Checks if the user is an administrator.
     * 
     * @return true if user has ROLE_ADMIN
     */
    public boolean isAdmin() {
        return hasRole(RoleName.ROLE_ADMIN);
    }

    /**
     * Checks if the user is an analyst.
     * 
     * @return true if user has ROLE_ANALISTA
     */
    public boolean isAnalyst() {
        return hasRole(RoleName.ROLE_ANALISTA);
    }

    /**
     * Checks if the user is an affiliate.
     * 
     * @return true if user has ROLE_AFILIADO
     */
    public boolean isAffiliate() {
        return hasRole(RoleName.ROLE_AFILIADO);
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        this.enabled = true;
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.enabled = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Affiliate getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Affiliate affiliate) {
        this.affiliate = affiliate;
    }
}
