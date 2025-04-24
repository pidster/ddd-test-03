package com.insurance.claim.claimservice.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class ClaimId {
    UUID value;
}