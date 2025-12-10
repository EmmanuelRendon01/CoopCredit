package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity for User persistence.
 * Maps domain model to database table.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username", unique = true),
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@NamedEntityGraph(
    name = "User.withRoles",
    attributeNodes = @NamedAttributeNode("roles")
)
@NamedEntityGraph(
    name = "User.full",
    attributeNodes = {
        @NamedAttributeNode("roles"),
        @NamedAttributeNode("affiliate")
    }
)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_roles_user")),
        inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    )
    private Set<RoleJpaEntity> roles = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", foreignKey = @ForeignKey(name = "fk_user_affiliate"))
    private AffiliateJpaEntity affiliate;

    // Constructors
    public UserJpaEntity() {
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

    public Set<RoleJpaEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleJpaEntity> roles) {
        this.roles = roles;
    }

    public AffiliateJpaEntity getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(AffiliateJpaEntity affiliate) {
        this.affiliate = affiliate;
    }
}
