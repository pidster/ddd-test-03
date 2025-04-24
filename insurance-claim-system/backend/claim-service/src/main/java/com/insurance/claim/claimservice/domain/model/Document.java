package com.insurance.claim.claimservice.domain.model;

import com.insurance.claim.claimservice.domain.valueobject.DocumentId;
import com.insurance.claim.claimservice.domain.valueobject.DocumentType;
import com.insurance.claim.common.domain.model.Entity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Document extends Entity<DocumentId> {
    private final String fileName;
    private final String contentType;
    private final String storageLocation;
    private final DocumentType documentType;
    private final LocalDateTime uploadedAt;
    private final String uploadedBy;
    
    public Document(DocumentId id, String fileName, String contentType, 
                   String storageLocation, DocumentType documentType, 
                   String uploadedBy) {
        super(id);
        this.fileName = fileName;
        this.contentType = contentType;
        this.storageLocation = storageLocation;
        this.documentType = documentType;
        this.uploadedAt = LocalDateTime.now();
        this.uploadedBy = uploadedBy;
    }
}