package com.insurance.claim.claimservice.domain.event;

import com.insurance.claim.common.domain.model.DomainEvent;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ClaimRejectedEvent implements DomainEvent {
    private final UUID claimId;
    private final String claimNumber;
    private final String rejectionReason;
    private final LocalDateTime rejectionDate;
    private final Instant occurredAt;
    
    public ClaimRejectedEvent(UUID claimId, String claimNumber, String rejectionReason, 
                             LocalDateTime rejectionDate) {
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.rejectionReason = rejectionReason;
        this.rejectionDate = rejectionDate;
        this.occurredAt = Instant.now();
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}