# Sequence Diagrams - Insurance Claim Workflows

This document provides detailed sequence diagrams for the main workflows in the Insurance Claim Processing context.

## 1. Successful Claim Processing Flow

This sequence diagram illustrates the complete flow of a successful claim from submission to payment and notification.

```mermaid
sequenceDiagram
    actor Customer
    participant ClaimPortal as Claim Portal
    participant ClaimService as Claim Service
    participant DocumentService as Document Service
    participant PolicyService as Policy Service
    participant PaymentService as Payment Service
    participant NotificationService as Notification Service
    actor Administrator
    actor ClaimsPersonnel
    
    Customer->>ClaimPortal: Submit claim with documents
    ClaimPortal->>ClaimService: SubmitClaimCommand
    ClaimService->>DocumentService: Store supporting documents
    DocumentService-->>ClaimService: Documents stored
    ClaimService->>ClaimService: Create new claim
    ClaimService-->>ClaimPortal: Claim received confirmation
    ClaimPortal-->>Customer: Display confirmation
    
    Note over ClaimService: ClaimSubmitted event published
    
    ClaimService->>Administrator: Assign claim for documentation check
    Administrator->>ClaimService: CheckDocumentationCommand
    Administrator->>DocumentService: Review documents
    DocumentService-->>Administrator: Document details
    Administrator->>ClaimService: Confirm documentation complete
    
    Note over ClaimService: ClaimRegistered event published
    
    ClaimService->>ClaimsPersonnel: Assign for insurance verification
    ClaimsPersonnel->>PolicyService: Request policy information
    PolicyService-->>ClaimsPersonnel: Return policy details
    ClaimsPersonnel->>ClaimService: CheckInsuranceCommand
    ClaimsPersonnel->>ClaimService: Confirm coverage valid
    
    Note over ClaimService: AssessmentPerformed event published
    
    ClaimsPersonnel->>ClaimService: AcceptClaimCommand
    ClaimService->>PaymentService: Schedule payment
    
    Note over ClaimService: PaymentScheduled event published
    
    PaymentService->>PaymentService: Process payment
    PaymentService-->>ClaimService: Confirm payment executed
    
    Note over ClaimService: PaymentPerformed event published
    
    ClaimService->>NotificationService: Request customer notification
    NotificationService->>Customer: Send payment notification
    
    Note over NotificationService: CustomerNotified event published
    
    Customer->>ClaimPortal: View claim status
    ClaimPortal->>ClaimService: Request claim details
    ClaimService-->>ClaimPortal: Return claim with payment details
    ClaimPortal-->>Customer: Display completed claim details
```

## 2. Claim Rejection Flow

This sequence diagram shows the flow when a claim is rejected during the assessment process.

```mermaid
sequenceDiagram
    actor Customer
    participant ClaimPortal as Claim Portal
    participant ClaimService as Claim Service
    participant DocumentService as Document Service
    participant PolicyService as Policy Service
    participant NotificationService as Notification Service
    actor Administrator
    actor ClaimsPersonnel
    
    Customer->>ClaimPortal: Submit claim with documents
    ClaimPortal->>ClaimService: SubmitClaimCommand
    ClaimService->>DocumentService: Store supporting documents
    DocumentService-->>ClaimService: Documents stored
    ClaimService->>ClaimService: Create new claim
    ClaimService-->>ClaimPortal: Claim received confirmation
    ClaimPortal-->>Customer: Display confirmation
    
    Note over ClaimService: ClaimSubmitted event published
    
    ClaimService->>Administrator: Assign claim for documentation check
    Administrator->>ClaimService: CheckDocumentationCommand
    Administrator->>DocumentService: Review documents
    DocumentService-->>Administrator: Document details
    Administrator->>ClaimService: Confirm documentation complete
    
    Note over ClaimService: ClaimRegistered event published
    
    ClaimService->>ClaimsPersonnel: Assign for insurance verification
    ClaimsPersonnel->>PolicyService: Request policy information
    PolicyService-->>ClaimsPersonnel: Return policy details
    ClaimsPersonnel->>ClaimService: CheckInsuranceCommand
    
    Note over ClaimsPersonnel: Determines claim is invalid
    
    ClaimsPersonnel->>ClaimService: RejectClaimCommand (with reason)
    
    Note over ClaimService: ClaimRejected event published
    
    ClaimService->>NotificationService: Request rejection notification
    NotificationService->>Customer: Send rejection notification with reason
    
    Note over NotificationService: CustomerNotified event published
    
    Customer->>ClaimPortal: View claim status
    ClaimPortal->>ClaimService: Request claim details
    ClaimService-->>ClaimPortal: Return claim with rejection details
    ClaimPortal-->>Customer: Display rejected claim with reason
```

## 3. Documentation Verification Flow

This sequence diagram focuses specifically on the documentation verification process, which is a critical step in claim processing.

```mermaid
sequenceDiagram
    actor Customer
    participant ClaimPortal as Claim Portal
    participant ClaimService as Claim Service
    participant DocumentService as Document Service
    actor Administrator
    
    Customer->>ClaimPortal: Submit claim with documents
    ClaimPortal->>ClaimService: SubmitClaimCommand
    ClaimService->>DocumentService: Store supporting documents
    DocumentService-->>ClaimService: Documents stored
    ClaimService->>ClaimService: Create new claim
    ClaimService-->>ClaimPortal: Claim received confirmation
    ClaimPortal-->>Customer: Display confirmation
    
    Note over ClaimService: ClaimSubmitted event published
    
    ClaimService->>Administrator: Assign claim for documentation check
    
    Administrator->>ClaimService: Request claim details
    ClaimService-->>Administrator: Return claim information
    Administrator->>DocumentService: Request document list
    DocumentService-->>Administrator: Return document metadata
    
    Note over Administrator: Review document completeness
    
    alt All required documents present and valid
        Administrator->>ClaimService: CheckDocumentationCommand (complete=true)
        ClaimService->>ClaimService: Update claim status to REGISTERED
        Note over ClaimService: ClaimRegistered event published
        ClaimService->>Administrator: Confirmation of registration
    else Missing or invalid documents
        Administrator->>ClaimService: Flag missing documents
        ClaimService->>NotificationService: Request additional documents
        NotificationService->>Customer: Notify about missing documents
        Customer->>ClaimPortal: Upload additional documents
        ClaimPortal->>DocumentService: Store new documents
        DocumentService-->>ClaimPortal: Documents stored
        ClaimPortal-->>Customer: Confirmation of upload
        Note over DocumentService: AdditionalDocumentsReceived event published
        DocumentService->>Administrator: Notification of new documents
        Administrator->>DocumentService: Review new documents
        Administrator->>ClaimService: CheckDocumentationCommand (complete=true)
        ClaimService->>ClaimService: Update claim status to REGISTERED
        Note over ClaimService: ClaimRegistered event published
    end
```

## 4. Cross-Context Integration Flow

This sequence diagram illustrates how the Claims Processing context integrates with other bounded contexts to complete the claim lifecycle.

```mermaid
sequenceDiagram
    participant CPC as Claims Processing Context
    participant PMC as Policy Management Context
    participant CC as Customer Context
    participant PC as Payment Context
    participant RC as Reporting Context
    
    Note over CPC: ClaimSubmitted event
    
    CPC->>PMC: Request policy verification
    PMC->>PMC: Validate policy status
    PMC->>PMC: Check coverage details
    PMC-->>CPC: Return policy verification result
    
    CPC->>CC: Request customer information
    CC-->>CPC: Return customer details
    
    Note over CPC: Assessment performed
    
    alt Claim Approved
        CPC->>PC: PaymentRequest (with approved amount)
        PC->>PC: Execute payment
        PC-->>CPC: Payment confirmation
        Note over CPC: PaymentPerformed event
        CPC->>CC: Update customer claim history
        CPC->>RC: Report claim payment
    else Claim Rejected
        Note over CPC: ClaimRejected event
        CPC->>CC: Update customer claim history
        CPC->>RC: Report claim rejection
    end
    
    CPC->>CC: Send notification to customer
```

## 5. Claim Status Inquiry Flow

This sequence diagram shows how a customer can check on the status of their claim.

```mermaid
sequenceDiagram
    actor Customer
    participant Portal as Customer Portal
    participant API as Claims API
    participant ClaimService as Claim Service
    participant EventStore as Event Store
    
    Customer->>Portal: Log in
    Portal->>API: Authenticate user
    API-->>Portal: User authenticated
    
    Customer->>Portal: Select "My Claims"
    Portal->>API: Request user's claims
    API->>ClaimService: GetCustomerClaims(customerId)
    ClaimService->>EventStore: Query claims by customer
    EventStore-->>ClaimService: Return claim events
    ClaimService->>ClaimService: Reconstruct claim states
    ClaimService-->>API: Return claims summary
    API-->>Portal: Display claims list
    Portal-->>Customer: Show claims dashboard
    
    Customer->>Portal: Select specific claim
    Portal->>API: GetClaimDetails(claimId)
    API->>ClaimService: GetClaimDetails(claimId)
    ClaimService->>EventStore: Query events for claim
    EventStore-->>ClaimService: Return all claim events
    ClaimService->>ClaimService: Reconstruct full claim state
    ClaimService-->>API: Return detailed claim information
    API-->>Portal: Process claim details
    Portal-->>Customer: Display claim timeline and status
    
    alt Claim status is "Additional Information Required"
        Customer->>Portal: Upload additional documents
        Portal->>API: SubmitAdditionalDocuments(claimId, documents)
        API->>ClaimService: AddClaimDocuments(claimId, documents)
        ClaimService->>EventStore: Store AdditionalDocumentsReceived event
        EventStore-->>ClaimService: Confirmation
        ClaimService-->>API: Documents accepted
        API-->>Portal: Update claim status
        Portal-->>Customer: Show confirmation message
    end
```

These sequence diagrams provide detailed views of the key workflows in the Insurance Claim Processing context, showing the interactions between users, services, and other contexts throughout the claim lifecycle.