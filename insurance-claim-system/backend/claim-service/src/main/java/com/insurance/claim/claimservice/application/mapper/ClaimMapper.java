package com.insurance.claim.claimservice.application.mapper;

import com.insurance.claim.claimservice.application.dto.ClaimResponseDto;
import com.insurance.claim.claimservice.application.dto.CreateClaimDto;
import com.insurance.claim.claimservice.application.dto.DocumentDto;
import com.insurance.claim.claimservice.domain.model.Claim;
import com.insurance.claim.claimservice.domain.model.Document;
import com.insurance.claim.claimservice.domain.valueobject.DocumentId;
import com.insurance.claim.claimservice.domain.valueobject.PolicyDetails;
import com.insurance.claim.common.domain.valueobjects.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClaimMapper {

    public PolicyDetails toPolicyDetails(CreateClaimDto createClaimDto) {
        return new PolicyDetails(
            createClaimDto.getPolicyNumber(),
            createClaimDto.getPolicyType(),
            createClaimDto.getPolicyStartDate(),
            createClaimDto.getPolicyEndDate(),
            Money.of(createClaimDto.getCoverageLimit(), createClaimDto.getCoverageCurrency()),
            Money.of(createClaimDto.getDeductibleAmount(), createClaimDto.getDeductibleCurrency())
        );
    }
    
    public Money toClaimAmount(CreateClaimDto createClaimDto) {
        return Money.of(createClaimDto.getClaimAmount(), createClaimDto.getClaimCurrency());
    }
    
    public ClaimResponseDto toClaimResponseDto(Claim claim) {
        return ClaimResponseDto.builder()
            .id(claim.getId().getValue())
            .claimNumber(claim.getClaimNumber())
            .policyHolderId(claim.getPolicyHolderId())
            .policyNumber(claim.getPolicyDetails().getPolicyNumber())
            .policyType(claim.getPolicyDetails().getPolicyType())
            .incidentDate(claim.getIncidentDate())
            .incidentDescription(claim.getIncidentDescription())
            .claimAmount(claim.getClaimAmount().getAmount())
            .currency(claim.getClaimAmount().getCurrency().name())
            .status(claim.getStatus())
            .submissionDate(claim.getSubmissionDate())
            .processingDate(claim.getProcessingDate())
            .documents(toDocumentDtoList(claim.getDocuments()))
            .rejectionReason(claim.getRejectionReason())
            .build();
    }
    
    public List<ClaimResponseDto> toClaimResponseDtoList(List<Claim> claims) {
        return claims.stream()
            .map(this::toClaimResponseDto)
            .collect(Collectors.toList());
    }
    
    private List<DocumentDto> toDocumentDtoList(List<Document> documents) {
        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
    
    private DocumentDto toDocumentDto(Document document) {
        return DocumentDto.builder()
            .id(document.getId().getValue())
            .fileName(document.getFileName())
            .contentType(document.getContentType())
            .documentType(document.getDocumentType())
            .uploadedAt(document.getUploadedAt())
            .uploadedBy(document.getUploadedBy())
            .build();
    }
    
    public Document toDocument(DocumentDto documentDto, String storageLocation) {
        return new Document(
            new DocumentId(documentDto.getId() != null ? documentDto.getId() : UUID.randomUUID()),
            documentDto.getFileName(),
            documentDto.getContentType(),
            storageLocation,
            documentDto.getDocumentType(),
            documentDto.getUploadedBy()
        );
    }
}