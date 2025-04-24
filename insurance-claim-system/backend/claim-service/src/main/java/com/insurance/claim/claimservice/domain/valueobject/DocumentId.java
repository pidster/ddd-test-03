package com.insurance.claim.claimservice.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class DocumentId {
    UUID value;
}