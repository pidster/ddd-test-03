package com.insurance.claim.claimservice.domain.event;

import com.insurance.claim.common.domain.model.DomainEvent;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Getter
public class ClaimCreatedEvent implements DomainEvent {
    private final UUID claimId;
    private final String claimNumber;
    private final String policyHolderId;
    private final LocalDateTime submissionDate;
    private final Instant occurredAt;
    
    public ClaimCreatedEvent(UUID claimId, String claimNumber, String policyHolderId, 
                            LocalDateTime submissionDate) {
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.policyHolderId = policyHolderId;
        this.submissionDate = submissionDate;
        this.occurredAt = Instant.now();
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}