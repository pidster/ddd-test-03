package com.insurance.claim.claimservice.domain.service;

import com.insurance.claim.claimservice.domain.model.Claim;
import com.insurance.claim.claimservice.domain.valueobject.PolicyDetails;
import com.insurance.claim.common.domain.valueobjects.Money;

import java.time.LocalDateTime;

/**
 * Domain service for handling claim creation and validation
 */
public interface ClaimDomainService {
    
    /**
     * Validates a claim against policy terms and conditions
     * @param claim The claim to validate
     * @return true if the claim is valid according to policy rules, false otherwise
     */
    boolean validateClaim(Claim claim);
    
    /**
     * Creates a new claim with the provided details
     */
    Claim createClaim(String policyHolderId, 
                     LocalDateTime incidentDate, 
                     String incidentDescription, 
                     PolicyDetails policyDetails, 
                     Money claimAmount);
}