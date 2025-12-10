package com.coopcredit.creditapplication.infrastructure.adapter.out.persistence.entity;

import com.coopcredit.creditapplication.domain.model.ApplicationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Credit Application persistence.
 * Maps domain model to database table.
 */
@Entity
@Table(name = "credit_applications", indexes = {
        @Index(name = "idx_credit_app_status", columnList = "status"),
        @Index(name = "idx_credit_app_affiliate", columnList = "affiliate_id"),
        @Index(name = "idx_credit_app_date", columnList = "application_date")
})
@NamedEntityGraph(name = "CreditApplication.withAffiliate", attributeNodes = @NamedAttributeNode("affiliate"))
@NamedEntityGraph(name = "CreditApplication.full", attributeNodes = {
        @NamedAttributeNode("affiliate"),
        @NamedAttributeNode("riskEvaluation")
})
public class CreditApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affiliate_id", nullable = false, foreignKey = @ForeignKey(name = "fk_credit_app_affiliate"))
    private AffiliateJpaEntity affiliate;

    @Column(name = "requested_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(name = "monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "requested_term_months", nullable = false)
    private Integer requestedTermMonths;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "current_debt", precision = 12, scale = 2)
    private BigDecimal currentDebt;

    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;

    @Column(name = "evaluation_comments", length = 1000)
    private String evaluationComments;

    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskEvaluationJpaEntity riskEvaluation;

    // Constructors
    public CreditApplicationJpaEntity() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AffiliateJpaEntity getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(AffiliateJpaEntity affiliate) {
        this.affiliate = affiliate;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Integer getRequestedTermMonths() {
        return requestedTermMonths;
    }

    public void setRequestedTermMonths(Integer requestedTermMonths) {
        this.requestedTermMonths = requestedTermMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getCurrentDebt() {
        return currentDebt;
    }

    public void setCurrentDebt(BigDecimal currentDebt) {
        this.currentDebt = currentDebt;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getEvaluationComments() {
        return evaluationComments;
    }

    public void setEvaluationComments(String evaluationComments) {
        this.evaluationComments = evaluationComments;
    }

    public RiskEvaluationJpaEntity getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskEvaluationJpaEntity riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
    }
}
