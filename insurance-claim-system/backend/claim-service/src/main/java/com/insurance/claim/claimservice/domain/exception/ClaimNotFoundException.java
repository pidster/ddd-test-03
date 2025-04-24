package com.insurance.claim.claimservice.domain.exception;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String message) {
        super(message);
    }
    
    public ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}