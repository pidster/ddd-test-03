package com.insurance.claim.claimservice.application.dto;

import com.insurance.claim.claimservice.domain.valueobject.DocumentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentDto {
    private UUID id;
    private String fileName;
    private String contentType;
    private DocumentType documentType;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
}