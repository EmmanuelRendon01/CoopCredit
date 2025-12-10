package com.coopcredit.creditapplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity: Affiliate
 * Pure POJO without infrastructure dependencies.
 * Represents a member of the credit cooperative.
 * 
 * Business Rules:
 * - Document number must be unique
 * - Salary must be greater than zero
 * - Only ACTIVE affiliates can request credits
 * - Minimum affiliation period of 3 months required for credit applications
 */
public class Affiliate {
    
    private Long id;
    private String documentType;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;
    private List<CreditApplication> creditApplications;

    public Affiliate() {
        this.creditApplications = new ArrayList<>();
        this.status = AffiliateStatus.ACTIVE;
        this.affiliationDate = LocalDate.now();
    }

    public Affiliate(String documentType, String documentNumber, String firstName, 
                    String lastName, String email, String phone, BigDecimal salary) {
        this();
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.salary = salary;
    }

    // Business Logic
    
    /**
     * Checks if the affiliate is currently active.
     * 
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isActive() {
        return this.status == AffiliateStatus.ACTIVE;
    }

    /**
     * Calculates the number of months since affiliation.
     * 
     * @return number of complete months as affiliate
     */
    public long getMonthsAsAffiliate() {
        return java.time.temporal.ChronoUnit.MONTHS.between(affiliationDate, LocalDate.now());
    }

    /**
     * Determines if the affiliate can request a credit based on status and tenure.
     * 
     * @param minMonths minimum required months as affiliate
     * @return true if affiliate is active and has minimum tenure
     */
    public boolean canRequestCredit(int minMonths) {
        return isActive() && getMonthsAsAffiliate() >= minMonths;
    }

    /**
     * Calculates the maximum credit amount based on salary.
     * Uses default multiplier of 10.
     * 
     * @return maximum allowed credit amount
     */
    public BigDecimal getMaxCreditAmount() {
        return getMaxCreditAmount(10);
    }

    /**
     * Calculates the maximum credit amount based on salary.
     * 
     * @param multiplier salary multiplier for max credit calculation
     * @return maximum allowed credit amount
     */
    public BigDecimal getMaxCreditAmount(int multiplier) {
        return salary.multiply(BigDecimal.valueOf(multiplier));
    }

    /**
     * Adds a credit application to this affiliate's application list.
     * 
     * @param application the credit application to add
     */
    public void addCreditApplication(CreditApplication application) {
        this.creditApplications.add(application);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getAffiliationDate() {
        return affiliationDate;
    }

    public void setAffiliationDate(LocalDate affiliationDate) {
        this.affiliationDate = affiliationDate;
    }

    public AffiliateStatus getStatus() {
        return status;
    }

    public void setStatus(AffiliateStatus status) {
        this.status = status;
    }

    public List<CreditApplication> getCreditApplications() {
        return creditApplications;
    }

    public void setCreditApplications(List<CreditApplication> creditApplications) {
        this.creditApplications = creditApplications;
    }
}
