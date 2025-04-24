package com.insurance.claim.claimservice.application.service;

import com.insurance.claim.claimservice.application.dto.ClaimResponseDto;
import com.insurance.claim.claimservice.application.dto.CreateClaimDto;
import com.insurance.claim.claimservice.application.dto.DocumentDto;
import com.insurance.claim.claimservice.application.mapper.ClaimMapper;
import com.insurance.claim.claimservice.domain.exception.ClaimNotFoundException;
import com.insurance.claim.claimservice.domain.model.Claim;
import com.insurance.claim.claimservice.domain.repository.ClaimRepository;
import com.insurance.claim.claimservice.domain.service.ClaimDomainService;
import com.insurance.claim.claimservice.domain.valueobject.ClaimId;
import com.insurance.claim.claimservice.domain.valueobject.ClaimStatus;
import com.insurance.claim.common.domain.valueobjects.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimApplicationService {

    private final ClaimRepository claimRepository;
    private final ClaimDomainService claimDomainService;
    private final ClaimMapper claimMapper;
    
    @Transactional
    public ClaimResponseDto createClaim(CreateClaimDto createClaimDto) {
        var policyDetails = claimMapper.toPolicyDetails(createClaimDto);
        var claimAmount = claimMapper.toClaimAmount(createClaimDto);
        
        Claim claim = claimDomainService.createClaim(
                createClaimDto.getPolicyHolderId(),
                createClaimDto.getIncidentDate(),
                createClaimDto.getIncidentDescription(),
                policyDetails,
                claimAmount);
        
        boolean isValid = claimDomainService.validateClaim(claim);
        
        if (!isValid) {
            claim.rejectClaim("Claim validation failed: policy not active or claim amount exceeds coverage");
        }
        
        Claim savedClaim = claimRepository.save(claim);
        return claimMapper.toClaimResponseDto(savedClaim);
    }
    
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimById(UUID claimId) {
        Claim claim = claimRepository.findById(new ClaimId(claimId))
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
        
        return claimMapper.toClaimResponseDto(claim);
    }
    
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimByNumber(String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with number: " + claimNumber));
        
        return claimMapper.toClaimResponseDto(claim);
    }
    
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByPolicyHolder(String policyHolderId) {
        List<Claim> claims = claimRepository.findByPolicyHolderId(policyHolderId);
        return claimMapper.toClaimResponseDtoList(claims);
    }
    
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByStatus(ClaimStatus status) {
        List<Claim> claims = claimRepository.findByStatus(status);
        return claimMapper.toClaimResponseDtoList(claims);
    }
    
    @Transactional
    public ClaimResponseDto processClaim(UUID claimId) {
        Claim claim = claimRepository.findById(new ClaimId(claimId))
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
        
        claim.processClaim();
        Claim savedClaim = claimRepository.save(claim);
        
        return claimMapper.toClaimResponseDto(savedClaim);
    }
    
    @Transactional
    public ClaimResponseDto approveClaim(UUID claimId, BigDecimal approvedAmount) {
        Claim claim = claimRepository.findById(new ClaimId(claimId))
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
        
        Money approvedMoney = Money.of(approvedAmount, claim.getClaimAmount().getCurrency());
        claim.approveClaim(approvedMoney);
        
        Claim savedClaim = claimRepository.save(claim);
        return claimMapper.toClaimResponseDto(savedClaim);
    }
    
    @Transactional
    public ClaimResponseDto rejectClaim(UUID claimId, String reason) {
        Claim claim = claimRepository.findById(new ClaimId(claimId))
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
        
        claim.rejectClaim(reason);
        Claim savedClaim = claimRepository.save(claim);
        
        return claimMapper.toClaimResponseDto(savedClaim);
    }
    
    @Transactional
    public ClaimResponseDto addDocumentToClaim(UUID claimId, DocumentDto documentDto, String storageLocation) {
        Claim claim = claimRepository.findById(new ClaimId(claimId))
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
        
        var document = claimMapper.toDocument(documentDto, storageLocation);
        claim.addDocument(document);
        
        Claim savedClaim = claimRepository.save(claim);
        return claimMapper.toClaimResponseDto(savedClaim);
    }
}