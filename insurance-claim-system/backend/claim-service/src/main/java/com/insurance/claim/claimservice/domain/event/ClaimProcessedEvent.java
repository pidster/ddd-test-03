package com.insurance.claim.claimservice.domain.event;

import com.insurance.claim.common.domain.model.DomainEvent;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ClaimProcessedEvent implements DomainEvent {
    private final UUID claimId;
    private final String claimNumber;
    private final LocalDateTime processingDate;
    private final Instant occurredAt;
    
    public ClaimProcessedEvent(UUID claimId, String claimNumber, LocalDateTime processingDate) {
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.processingDate = processingDate;
        this.occurredAt = Instant.now();
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}