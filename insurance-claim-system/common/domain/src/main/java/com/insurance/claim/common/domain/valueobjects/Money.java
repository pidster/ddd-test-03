package com.insurance.claim.common.domain.valueobjects;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Money {
    BigDecimal amount;
    Currency currency;
    
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
    
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract money with different currencies");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public boolean isLessThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }
        return this.amount.compareTo(other.amount) < 0;
    }
}