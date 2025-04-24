package com.insurance.claim.claimservice.domain.service.impl;

import com.insurance.claim.claimservice.domain.model.Claim;
import com.insurance.claim.claimservice.domain.service.ClaimDomainService;
import com.insurance.claim.claimservice.domain.valueobject.PolicyDetails;
import com.insurance.claim.common.domain.valueobjects.Money;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ClaimDomainServiceImpl implements ClaimDomainService {

    @Override
    public boolean validateClaim(Claim claim) {
        // Check if policy was active on incident date
        if (!claim.getPolicyDetails().isActive(claim.getIncidentDate().toLocalDate())) {
            return false;
        }
        
        // Check if claim amount is within coverage limit
        if (claim.getClaimAmount().isGreaterThan(claim.getPolicyDetails().getCoverageLimit())) {
            return false;
        }
        
        // Additional validation rules can be added here
        
        return true;
    }

    @Override
    public Claim createClaim(String policyHolderId, 
                           LocalDateTime incidentDate, 
                           String incidentDescription, 
                           PolicyDetails policyDetails, 
                           Money claimAmount) {
        return Claim.create(policyHolderId, incidentDate, incidentDescription, 
                          policyDetails, claimAmount);
    }
}