package com.insurance.claim.claimservice.domain.valueobject;

import com.insurance.claim.common.domain.valueobjects.Money;
import lombok.Value;

import java.time.LocalDate;

@Value
public class PolicyDetails {
    String policyNumber;
    String policyType;
    LocalDate startDate;
    LocalDate endDate;
    Money coverageLimit;
    Money deductible;
    
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    public boolean isActive() {
        return isActive(LocalDate.now());
    }
}