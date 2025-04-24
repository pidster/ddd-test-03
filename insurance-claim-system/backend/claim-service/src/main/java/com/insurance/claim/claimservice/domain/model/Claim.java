package com.insurance.claim.claimservice.domain.model;

import com.insurance.claim.claimservice.domain.event.ClaimCreatedEvent;
import com.insurance.claim.claimservice.domain.event.ClaimProcessedEvent;
import com.insurance.claim.claimservice.domain.event.ClaimRejectedEvent;
import com.insurance.claim.claimservice.domain.valueobject.ClaimId;
import com.insurance.claim.claimservice.domain.valueobject.ClaimStatus;
import com.insurance.claim.claimservice.domain.valueobject.PolicyDetails;
import com.insurance.claim.common.domain.model.AggregateRoot;
import com.insurance.claim.common.domain.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Claim extends AggregateRoot<ClaimId> {
    private final String claimNumber;
    private final String policyHolderId;
    private final LocalDateTime incidentDate;
    private final String incidentDescription;
    private final PolicyDetails policyDetails;
    private final List<Document> documents;
    private Money claimAmount;
    private ClaimStatus status;
    private String rejectionReason;
    private LocalDateTime submissionDate;
    private LocalDateTime processingDate;
    
    private Claim(ClaimId id, String claimNumber, String policyHolderId, 
                 LocalDateTime incidentDate, String incidentDescription,
                 PolicyDetails policyDetails, Money claimAmount) {
        super(id);
        this.claimNumber = claimNumber;
        this.policyHolderId = policyHolderId;
        this.incidentDate = incidentDate;
        this.incidentDescription = incidentDescription;
        this.policyDetails = policyDetails;
        this.claimAmount = claimAmount;
        this.status = ClaimStatus.SUBMITTED;
        this.documents = new ArrayList<>();
        this.submissionDate = LocalDateTime.now();
    }
    
    public static Claim create(String policyHolderId, LocalDateTime incidentDate,
                              String incidentDescription, PolicyDetails policyDetails,
                              Money claimAmount) {
        ClaimId claimId = new ClaimId(UUID.randomUUID());
        String claimNumber = generateClaimNumber();
        
        Claim claim = new Claim(claimId, claimNumber, policyHolderId, incidentDate, 
                               incidentDescription, policyDetails, claimAmount);
        
        claim.registerEvent(new ClaimCreatedEvent(claimId.getValue(), claim.getClaimNumber(), 
                                               policyHolderId, claim.getSubmissionDate()));
        
        return claim;
    }
    
    public void addDocument(Document document) {
        documents.add(document);
    }
    
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }
    
    public void processClaim() {
        if (status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Claim cannot be processed as it's not in SUBMITTED state");
        }
        
        status = ClaimStatus.PROCESSING;
        processingDate = LocalDateTime.now();
        
        registerEvent(new ClaimProcessedEvent(getId().getValue(), claimNumber, processingDate));
    }
    
    public void approveClaim(Money approvedAmount) {
        if (status != ClaimStatus.PROCESSING) {
            throw new IllegalStateException("Claim cannot be approved as it's not in PROCESSING state");
        }
        
        status = ClaimStatus.APPROVED;
        claimAmount = approvedAmount;
    }
    
    public void rejectClaim(String reason) {
        if (status != ClaimStatus.PROCESSING) {
            throw new IllegalStateException("Claim cannot be rejected as it's not in PROCESSING state");
        }
        
        status = ClaimStatus.REJECTED;
        rejectionReason = reason;
        
        registerEvent(new ClaimRejectedEvent(getId().getValue(), claimNumber, reason, LocalDateTime.now()));
    }
    
    private static String generateClaimNumber() {
        // Format: CLM-YYYYMMDD-XXXXX (where XXXXX is random)
        return String.format("CLM-%tY%<tm%<td-%05d", 
                            LocalDateTime.now(), (int) (Math.random() * 100000));
    }
}