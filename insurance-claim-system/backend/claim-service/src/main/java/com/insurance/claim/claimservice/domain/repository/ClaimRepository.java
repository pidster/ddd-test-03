package com.insurance.claim.claimservice.domain.repository;

import com.insurance.claim.claimservice.domain.model.Claim;
import com.insurance.claim.claimservice.domain.valueobject.ClaimId;
import com.insurance.claim.claimservice.domain.valueobject.ClaimStatus;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository {
    Claim save(Claim claim);
    Optional<Claim> findById(ClaimId claimId);
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByPolicyHolderId(String policyHolderId);
    List<Claim> findByStatus(ClaimStatus status);
    List<Claim> findAll();
}