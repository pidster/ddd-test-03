# Insurance Claim Domain Model

This document details the key domain components identified during our event storming session, providing a foundation for implementing the insurance claim processing system.

## Domain Events

Domain events represent significant occurrences within the system that domain experts care about.

```mermaid
classDiagram
    class DomainEvent {
        <<interface>>
        +DateTime occurredOn
        +String correlationId
    }
    
    class ClaimSubmitted {
        +UUID claimId
        +DateTime submissionDate
        +String submissionChannel
        +UUID customerId
    }
    
    class ClaimRegistered {
        +UUID claimId
        +DateTime registrationDate
        +Boolean isDocumentationComplete
    }
    
    class AssessmentPerformed {
        +UUID claimId
        +DateTime assessmentDate
        +Boolean coverageVerified
    }
    
    class PaymentScheduled {
        +UUID claimId
        +UUID paymentId
        +Money amount
        +DateTime scheduledDate
    }
    
    class PaymentPerformed {
        +UUID paymentId
        +Money amount
        +DateTime paymentDate
        +String paymentMethod
    }
    
    class ClaimRejected {
        +UUID claimId
        +DateTime rejectionDate
        +String rejectionReason
    }
    
    class CustomerNotified {
        +UUID customerId
        +UUID claimId
        +DateTime notificationDate
        +String notificationMethod
    }
    
    DomainEvent <|-- ClaimSubmitted
    DomainEvent <|-- ClaimRegistered
    DomainEvent <|-- AssessmentPerformed
    DomainEvent <|-- PaymentScheduled
    DomainEvent <|-- PaymentPerformed
    DomainEvent <|-- ClaimRejected
    DomainEvent <|-- CustomerNotified
```

## Aggregates

Aggregates are clusters of domain objects that can be treated as a single unit. 

```mermaid
classDiagram
    class Claim {
        +UUID id
        +ClaimStatus status
        +DateTime submissionDate
        +Customer customer
        +Policy policy
        +List~Document~ supportingDocuments
        +Money claimAmount
        +String description
        +submitClaim()
        +checkDocumentation()
        +register()
        +verifyInsurance()
        +performAssessment()
        +accept()
        +reject(reason)
        +schedulePayment(amount)
    }
    
    class Payment {
        +UUID id
        +UUID claimId
        +Money amount
        +PaymentStatus status
        +DateTime scheduledDate
        +DateTime processedDate
        +String paymentMethod
        +schedule()
        +perform()
    }
    
    class Notification {
        +UUID id
        +UUID recipientId
        +String message
        +NotificationType type
        +NotificationChannel channel
        +NotificationStatus status
        +DateTime scheduledDate
        +DateTime sentDate
        +send()
    }
    
    Claim "1" --> "0..1" Payment : schedules
    Claim "1" --> "1..*" Notification : triggers
```

## Commands

Commands represent business operations that can be performed in the system.

```mermaid
classDiagram
    class Command {
        <<interface>>
    }
    
    class SubmitClaimCommand {
        +UUID customerId
        +UUID policyId
        +String description
        +Money claimAmount
        +List~Document~ supportingDocuments
        +String submissionChannel
    }
    
    class CheckDocumentationCommand {
        +UUID claimId
        +UUID administratorId
    }
    
    class CheckInsuranceCommand {
        +UUID claimId
        +UUID claimsPersonId
    }
    
    class AcceptClaimCommand {
        +UUID claimId
        +UUID claimsPersonId
        +Money approvedAmount
        +DateTime scheduledPaymentDate
    }
    
    class RejectClaimCommand {
        +UUID claimId
        +UUID claimsPersonId
        +String rejectionReason
    }
    
    class PerformPaymentCommand {
        +UUID paymentId
        +String paymentMethod
    }
    
    class NotifyCustomerCommand {
        +UUID claimId
        +UUID customerId
        +String notificationMethod
        +String messageTemplate
    }
    
    Command <|-- SubmitClaimCommand
    Command <|-- CheckDocumentationCommand
    Command <|-- CheckInsuranceCommand
    Command <|-- AcceptClaimCommand
    Command <|-- RejectClaimCommand
    Command <|-- PerformPaymentCommand
    Command <|-- NotifyCustomerCommand
```

## User Roles and Actors

```mermaid
classDiagram
    class User {
        <<abstract>>
        +UUID id
        +String name
        +String email
        +String role
    }
    
    class Customer {
        +List~Policy~ policies
        +ContactInformation contactInfo
        +submitClaim()
        +viewClaimStatus()
    }
    
    class Administrator {
        +checkDocumentCompleteness()
        +registerClaim()
    }
    
    class ClaimsDepartmentPerson {
        +String department
        +checkInsuranceCoverage()
        +performAssessment()
        +acceptClaim()
        +rejectClaim()
    }
    
    User <|-- Customer
    User <|-- Administrator
    User <|-- ClaimsDepartmentPerson
```

## Value Objects

```mermaid
classDiagram
    class Money {
        +BigDecimal amount
        +Currency currency
    }
    
    class Document {
        +UUID id
        +String name
        +String type
        +DateTime uploadDate
        +byte[] content
        +Boolean verified
    }
    
    class ContactInformation {
        +String email
        +String phoneNumber
        +Address mailingAddress
    }
    
    class Address {
        +String street
        +String city
        +String state
        +String postalCode
        +String country
    }
```

## Enums

```mermaid
classDiagram
    class ClaimStatus {
        <<enumeration>>
        SUBMITTED
        REGISTERED
        UNDER_ASSESSMENT
        ACCEPTED
        REJECTED
        PAYMENT_SCHEDULED
        PAYMENT_COMPLETED
        CLOSED
    }
    
    class PaymentStatus {
        <<enumeration>>
        SCHEDULED
        PROCESSING
        COMPLETED
        FAILED
    }
    
    class NotificationStatus {
        <<enumeration>>
        PENDING
        SENT
        DELIVERED
        FAILED
    }
    
    class NotificationType {
        <<enumeration>>
        CLAIM_RECEIVED
        CLAIM_REGISTERED
        CLAIM_ACCEPTED
        CLAIM_REJECTED
        PAYMENT_SCHEDULED
        PAYMENT_COMPLETED
    }
    
    class NotificationChannel {
        <<enumeration>>
        EMAIL
        MAIL
        SMS
        PUSH
    }
```

## Strategic Design Considerations

The event storming session raised important questions about the boundaries of our domain model:

1. **Policy Management Context vs Dedicated Bounded Context:**
   - Should the claim processing capabilities be part of the Policy Management context?
   - Or should it be separated into its own Bounded Context?

```mermaid
graph TD
    subgraph "Option 1: Extended Policy Management Context"
    PM[Policy Management]
    PM --> PC[Policy Creation]
    PM --> PP[Premium Payments]
    PM --> CP[Claim Processing]
    end
    
    subgraph "Option 2: Separate Bounded Contexts"
    PMC[Policy Management Context]
    PMC --> PC2[Policy Creation]
    PMC --> PP2[Premium Payments]
    
    CPC[Claim Processing Context]
    CPC --> CS[Claim Submission]
    CPC --> CA[Claim Assessment]
    CPC --> CP2[Claim Payment]
    
    PMC -.->|"Context Map"| CPC
    end
```

2. **Integration Points:**
   - If separated, the Claim Processing context would need:
     - Policy information from the Policy Management context
     - Customer information from the Customer context
     - Payment capabilities from the Payment context

These architectural decisions should be made with domain experts to ensure the system accurately reflects the insurance business domain.