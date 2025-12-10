package com.coopcredit.creditapplication.application.dto;

import com.coopcredit.creditapplication.domain.model.AffiliateStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for affiliate query responses.
 * 
 * Contains all affiliate information for display purposes.
 */
public class AffiliateResponse {

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
    private Long monthsAsAffiliate;
    private Integer totalCreditApplications;

    // Constructors
    public AffiliateResponse() {
    }

    public AffiliateResponse(Long id, String documentType, String documentNumber,
                            String firstName, String lastName, String email, String phone,
                            BigDecimal salary, LocalDate affiliationDate, AffiliateStatus status) {
        this.id = id;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.salary = salary;
        this.affiliationDate = affiliationDate;
        this.status = status;
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

    public Long getMonthsAsAffiliate() {
        return monthsAsAffiliate;
    }

    public void setMonthsAsAffiliate(Long monthsAsAffiliate) {
        this.monthsAsAffiliate = monthsAsAffiliate;
    }

    public Integer getTotalCreditApplications() {
        return totalCreditApplications;
    }

    public void setTotalCreditApplications(Integer totalCreditApplications) {
        this.totalCreditApplications = totalCreditApplications;
    }
}
