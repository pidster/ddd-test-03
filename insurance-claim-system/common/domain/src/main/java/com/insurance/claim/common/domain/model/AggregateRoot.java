package com.insurance.claim.common.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID> extends Entity<ID> {
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected AggregateRoot(ID id) {
        super(id);
    }
    
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearEvents() {
        domainEvents.clear();
    }
}