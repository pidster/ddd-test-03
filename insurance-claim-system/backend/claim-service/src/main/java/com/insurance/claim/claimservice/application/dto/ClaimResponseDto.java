package com.insurance.claim.claimservice.application.dto;

import com.insurance.claim.claimservice.domain.valueobject.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ClaimResponseDto {
    private UUID id;
    private String claimNumber;
    private String policyHolderId;
    private String policyNumber;
    private String policyType;
    private LocalDateTime incidentDate;
    private String incidentDescription;
    private BigDecimal claimAmount;
    private String currency;
    private ClaimStatus status;
    private LocalDateTime submissionDate;
    private LocalDateTime processingDate;
    private List<DocumentDto> documents;
    private String rejectionReason;
}