package com.insurance.claim.claimservice.application.dto;

import com.insurance.claim.common.domain.valueobjects.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateClaimDto {
    private String policyHolderId;
    private LocalDateTime incidentDate;
    private String incidentDescription;
    
    // Policy details
    private String policyNumber;
    private String policyType;
    private LocalDate policyStartDate;
    private LocalDate policyEndDate;
    private BigDecimal coverageLimit;
    private Currency coverageCurrency;
    private BigDecimal deductibleAmount;
    private Currency deductibleCurrency;
    
    // Claim amount
    private BigDecimal claimAmount;
    private Currency claimCurrency;
}