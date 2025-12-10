package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * JPA Entity for Risk Evaluation persistence.
 * Maps domain model to database table.
 */
@Entity
@Table(name = "risk_evaluations", indexes = {
    @Index(name = "idx_risk_eval_credit_app", columnList = "credit_application_id", unique = true),
    @Index(name = "idx_risk_eval_date", columnList = "evaluation_date")
})
public class RiskEvaluationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_application_id", nullable = false, unique = true, 
                foreignKey = @ForeignKey(name = "fk_risk_eval_credit_app"))
    private CreditApplicationJpaEntity creditApplication;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;

    @Column(nullable = false, length = 50)
    private String recommendation;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    // Constructors
    public RiskEvaluationJpaEntity() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplicationJpaEntity getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplicationJpaEntity creditApplication) {
        this.creditApplication = creditApplication;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }
}
