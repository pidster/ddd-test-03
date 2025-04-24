package com.insurance.claim.common.domain.model;

import java.time.Instant;

public interface DomainEvent {
    /**
     * The time when this event occurred
     */
    Instant getOccurredAt();
}