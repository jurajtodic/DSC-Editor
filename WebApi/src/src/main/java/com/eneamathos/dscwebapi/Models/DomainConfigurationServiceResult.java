package com.eneamathos.dscwebapi.Models;

import com.eneamathos.dscwebapi.ANTLR4.ValidationResult;

import java.util.List;

public class DomainConfigurationServiceResult {
    private DomainConfiguration DomainConfiguration;
    private ValidationResult ValidationResult;
    private List<ValidationResult> ValidationResultList;

    public DomainConfiguration getDomainConfiguration() {
        return DomainConfiguration;
    }

    public ValidationResult getValidationResult() {
        return ValidationResult;
    }


    public void setDomainConfiguration(DomainConfiguration domainConfiguration) {
        this.DomainConfiguration = domainConfiguration;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.ValidationResult = validationResult;
    }

    public List<ValidationResult> getValidationResultList() {
        return ValidationResultList;
    }

    public void setValidationResultList(List<ValidationResult> validationResultList) {
        ValidationResultList = validationResultList;
    }
}
