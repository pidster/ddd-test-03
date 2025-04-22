# API Specifications - Insurance Claim Processing

This document defines the REST API specifications for the Insurance Claim Processing context, following RESTful principles and OpenAPI 3.0 standards.

## API Overview

The Claims API provides interfaces for:
1. Submitting and managing insurance claims
2. Uploading and retrieving claim documents
3. Checking claim status
4. Processing claim decisions
5. Managing claim payments
6. Handling notifications

## Base URL

```
https://api.insurance-company.com/claims/v1
```

## Authentication & Authorization

All API endpoints require authentication using OAuth 2.0 / JWT tokens. Different endpoints require different roles:
- `customer`: For policyholder-accessible endpoints
- `admin`: For administrative staff
- `claims-handler`: For claims department personnel
- `system`: For internal system-to-system communication

## Common HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - The request has succeeded |
| 201 | Created - A new resource has been created |
| 400 | Bad Request - Invalid input parameters |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource state conflict |
| 422 | Unprocessable Entity - Validation errors |
| 500 | Internal Server Error - Server-side error |

## API Endpoints

### Claims Management

#### Submit a new claim

```
POST /claims
```

**Authorization:** `customer`

**Request Body:**
```json
{
  "policyNumber": "POL-1234567890",
  "claimType": "HOME_DAMAGE",
  "incidentDate": "2025-04-15T14:30:00Z",
  "description": "Water damage due to burst pipe in bathroom",
  "damageAmount": {
    "value": 5000.00,
    "currency": "USD"
  },
  "contactPreference": "EMAIL"
}
```

**Response:** `201 Created`
```json
{
  "claimId": "CLM-20250422-12345",
  "status": "SUBMITTED",
  "submissionDate": "2025-04-22T10:15:30Z",
  "links": [
    {
      "rel": "self",
      "href": "/claims/CLM-20250422-12345"
    },
    {
      "rel": "documents",
      "href": "/claims/CLM-20250422-12345/documents"
    },
    {
      "rel": "history",
      "href": "/claims/CLM-20250422-12345/history"
    }
  ]
}
```

#### Get claim details

```
GET /claims/{claimId}
```

**Authorization:** `customer`, `admin`, `claims-handler`

**Response:** `200 OK`
```json
{
  "claimId": "CLM-20250422-12345",
  "policyNumber": "POL-1234567890",
  "customer": {
    "id": "CUS-98765",
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  "claimType": "HOME_DAMAGE",
  "incidentDate": "2025-04-15T14:30:00Z",
  "submissionDate": "2025-04-22T10:15:30Z", 
  "description": "Water damage due to burst pipe in bathroom",
  "damageAmount": {
    "value": 5000.00,
    "currency": "USD"
  },
  "status": "REGISTERED",
  "documents": [
    {
      "documentId": "DOC-001",
      "name": "damage_photo_1.jpg",
      "uploadDate": "2025-04-22T10:20:15Z",
      "verified": true
    },
    {
      "documentId": "DOC-002",
      "name": "plumber_invoice.pdf",
      "uploadDate": "2025-04-22T10:22:40Z",
      "verified": true
    }
  ],
  "timeline": [
    {
      "timestamp": "2025-04-22T10:15:30Z",
      "event": "CLAIM_SUBMITTED",
      "actor": "Customer",
      "notes": "Initial claim submission"
    },
    {
      "timestamp": "2025-04-22T14:05:10Z",
      "event": "CLAIM_REGISTERED",
      "actor": "Admin",
      "notes": "All documentation completed and verified"
    }
  ],
  "links": [
    {
      "rel": "self",
      "href": "/claims/CLM-20250422-12345"
    },
    {
      "rel": "documents",
      "href": "/claims/CLM-20250422-12345/documents"
    },
    {
      "rel": "history",
      "href": "/claims/CLM-20250422-12345/history"
    }
  ]
}
```

#### Get customer claims

```
GET /customers/{customerId}/claims
```

**Authorization:** `customer`, `admin`, `claims-handler`

**Query Parameters:**
- `status` (optional): Filter by claim status
- `fromDate` (optional): Filter by submission date (start)
- `toDate` (optional): Filter by submission date (end)
- `page` (optional): Page number for pagination, default 0
- `size` (optional): Page size for pagination, default 20

**Response:** `200 OK`
```json
{
  "claims": [
    {
      "claimId": "CLM-20250422-12345",
      "policyNumber": "POL-1234567890",
      "claimType": "HOME_DAMAGE",
      "submissionDate": "2025-04-22T10:15:30Z",
      "status": "REGISTERED",
      "damageAmount": {
        "value": 5000.00,
        "currency": "USD"
      }
    },
    {
      "claimId": "CLM-20250410-54321",
      "policyNumber": "POL-1234567890",
      "claimType": "PERSONAL_LIABILITY",
      "submissionDate": "2025-04-10T09:45:22Z",
      "status": "PAYMENT_SCHEDULED",
      "damageAmount": {
        "value": 1500.00,
        "currency": "USD"
      }
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

#### Update claim status (Admin)

```
PATCH /claims/{claimId}/status
```

**Authorization:** `admin`, `claims-handler`

**Request Body:**
```json
{
  "status": "REGISTERED",
  "notes": "All documentation verified",
  "actorId": "ADM-001"
}
```

**Response:** `200 OK`
```json
{
  "claimId": "CLM-20250422-12345",
  "status": "REGISTERED",
  "lastUpdated": "2025-04-22T14:05:10Z",
  "actor": "Administrator"
}
```

### Document Management

#### Upload claim documents

```
POST /claims/{claimId}/documents
```

**Authorization:** `customer`, `admin`

**Request Body:** `multipart/form-data`
- `file`: The document file
- `documentType`: Type of document (e.g., "DAMAGE_PHOTO", "INVOICE", "REPORT")
- `description`: Optional description

**Response:** `201 Created`
```json
{
  "documentId": "DOC-003",
  "claimId": "CLM-20250422-12345",
  "fileName": "additional_damage_photo.jpg",
  "documentType": "DAMAGE_PHOTO",
  "uploadDate": "2025-04-22T15:35:20Z",
  "status": "UPLOADED",
  "verified": false,
  "links": [
    {
      "rel": "self",
      "href": "/claims/CLM-20250422-12345/documents/DOC-003"
    }
  ]
}
```

#### Get claim documents

```
GET /claims/{claimId}/documents
```

**Authorization:** `customer`, `admin`, `claims-handler`

**Response:** `200 OK`
```json
{
  "documents": [
    {
      "documentId": "DOC-001",
      "fileName": "damage_photo_1.jpg",
      "documentType": "DAMAGE_PHOTO",
      "uploadDate": "2025-04-22T10:20:15Z",
      "status": "VERIFIED",
      "verified": true,
      "fileSize": 2457600,
      "mimeType": "image/jpeg"
    },
    {
      "documentId": "DOC-002",
      "fileName": "plumber_invoice.pdf",
      "documentType": "INVOICE",
      "uploadDate": "2025-04-22T10:22:40Z",
      "status": "VERIFIED",
      "verified": true,
      "fileSize": 1048576,
      "mimeType": "application/pdf"
    }
  ],
  "requiredDocumentTypes": [
    "DAMAGE_PHOTO",
    "INVOICE",
    "REPAIR_ESTIMATE"
  ],
  "missingDocumentTypes": [
    "REPAIR_ESTIMATE"
  ]
}
```

#### Download a document

```
GET /claims/{claimId}/documents/{documentId}/content
```

**Authorization:** `customer`, `admin`, `claims-handler`

**Response:** `200 OK`
- Content-Type: The MIME type of the document
- Body: The raw document content

### Assessment and Decision

#### Verify insurance coverage

```
POST /claims/{claimId}/verify-coverage
```

**Authorization:** `claims-handler`

**Request Body:**
```json
{
  "verifierId": "CLM-HANDLER-002",
  "coverageConfirmed": true,
  "policyLimits": {
    "value": 10000.00,
    "currency": "USD"
  },
  "deductible": {
    "value": 500.00,
    "currency": "USD"
  },
  "notes": "Coverage confirmed for water damage under homeowner's policy"
}
```

**Response:** `200 OK`
```json
{
  "claimId": "CLM-20250422-12345",
  "assessmentId": "ASS-001",
  "status": "COVERAGE_VERIFIED",
  "timestamp": "2025-04-23T09:15:40Z",
  "coverageConfirmed": true
}
```

#### Accept claim and schedule payment

```
POST /claims/{claimId}/accept
```

**Authorization:** `claims-handler`

**Request Body:**
```json
{
  "handlerId": "CLM-HANDLER-002",
  "approvedAmount": {
    "value": 4500.00,
    "currency": "USD"
  },
  "deductibleApplied": {
    "value": 500.00,
    "currency": "USD"
  },
  "paymentMethod": "BANK_TRANSFER",
  "scheduledDate": "2025-04-25T00:00:00Z",
  "bankAccount": {
    "accountHolder": "John Doe",
    "accountNumber": "XXXX-XXXX-XXXX-1234",
    "bankCode": "BANKUS12"
  },
  "notes": "Claim approved with standard deductible"
}
```

**Response:** `200 OK`
```json
{
  "claimId": "CLM-20250422-12345",
  "decisionId": "DEC-001",
  "status": "ACCEPTED",
  "timestamp": "2025-04-23T11:30:15Z",
  "approvedAmount": {
    "value": 4500.00,
    "currency": "USD"
  },
  "paymentId": "PAY-001",
  "paymentStatus": "SCHEDULED",
  "scheduledPaymentDate": "2025-04-25T00:00:00Z"
}
```

#### Reject claim

```
POST /claims/{claimId}/reject
```

**Authorization:** `claims-handler`

**Request Body:**
```json
{
  "handlerId": "CLM-HANDLER-002",
  "rejectionReason": "POLICY_EXCLUSION",
  "rejectionDetails": "The damage was caused by long-term water leakage, which is excluded under policy terms section 3.2.",
  "appealInstructions": "You may appeal this decision by submitting additional evidence within 30 days.",
  "notes": "Customer informed about exclusions and appeal process"
}
```

**Response:** `200 OK`
```json
{
  "claimId": "CLM-20250422-12345",
  "decisionId": "DEC-002",
  "status": "REJECTED",
  "timestamp": "2025-04-23T11:35:20Z",
  "rejectionReason": "POLICY_EXCLUSION",
  "appealDeadline": "2025-05-23T11:35:20Z"
}
```

### Payment Management

#### Get payment details

```
GET /claims/{claimId}/payments
```

**Authorization:** `customer`, `admin`, `claims-handler`

**Response:** `200 OK`
```json
{
  "payments": [
    {
      "paymentId": "PAY-001",
      "claimId": "CLM-20250422-12345",
      "amount": {
        "value": 4500.00,
        "currency": "USD"
      },
      "status": "COMPLETED",
      "scheduledDate": "2025-04-25T00:00:00Z",
      "processedDate": "2025-04-25T07:15:30Z",
      "paymentMethod": "BANK_TRANSFER",
      "reference": "INS-PAY-4500-25042025"
    }
  ]
}
```

#### Process payment (System)

```
POST /payments/{paymentId}/process
```

**Authorization:** `system`

**Request Body:**
```json
{
  "transactionId": "TXN-985632147",
  "processingTimestamp": "2025-04-25T07:15:30Z",
  "successful": true
}
```

**Response:** `200 OK`
```json
{
  "paymentId": "PAY-001",
  "status": "COMPLETED",
  "processedDate": "2025-04-25T07:15:30Z",
  "transactionId": "TXN-985632147"
}
```

### Notification Management

#### Get customer notifications

```
GET /customers/{customerId}/notifications
```

**Authorization:** `customer`, `admin`

**Query Parameters:**
- `read` (optional): Filter by read status (true/false)
- `page` (optional): Page number for pagination, default 0
- `size` (optional): Page size for pagination, default 20

**Response:** `200 OK`
```json
{
  "notifications": [
    {
      "notificationId": "NOT-001",
      "customerId": "CUS-98765",
      "claimId": "CLM-20250422-12345",
      "timestamp": "2025-04-22T10:16:00Z",
      "type": "CLAIM_RECEIVED",
      "title": "Claim Submitted Successfully",
      "message": "Your claim CLM-20250422-12345 has been received and will be processed shortly.",
      "read": true,
      "channel": "EMAIL"
    },
    {
      "notificationId": "NOT-002",
      "customerId": "CUS-98765",
      "claimId": "CLM-20250422-12345",
      "timestamp": "2025-04-23T14:30:00Z",
      "type": "PAYMENT_SCHEDULED",
      "title": "Claim Approved - Payment Scheduled",
      "message": "Your claim CLM-20250422-12345 has been approved. Payment of $4,500.00 has been scheduled for April 25, 2025.",
      "read": false,
      "channel": "EMAIL"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

#### Mark notification as read

```
PATCH /notifications/{notificationId}
```

**Authorization:** `customer`

**Request Body:**
```json
{
  "read": true
}
```

**Response:** `200 OK`
```json
{
  "notificationId": "NOT-002",
  "read": true,
  "lastUpdated": "2025-04-23T15:45:10Z"
}
```

## Event Webhooks

The API supports webhook notifications for important claim events. Clients can register webhook endpoints to receive real-time updates.

### Register webhook endpoint

```
POST /webhooks
```

**Authorization:** `admin`, `system`

**Request Body:**
```json
{
  "url": "https://client-system.example.com/callbacks/insurance-claims",
  "events": [
    "CLAIM_SUBMITTED",
    "CLAIM_REGISTERED",
    "CLAIM_ACCEPTED",
    "PAYMENT_COMPLETED"
  ],
  "secret": "Guw7HicvJSkmNU$59X"
}
```

**Response:** `201 Created`
```json
{
  "webhookId": "WH-001",
  "url": "https://client-system.example.com/callbacks/insurance-claims",
  "events": [
    "CLAIM_SUBMITTED",
    "CLAIM_REGISTERED",
    "CLAIM_ACCEPTED",
    "PAYMENT_COMPLETED"
  ],
  "status": "ACTIVE",
  "createdAt": "2025-04-22T16:20:00Z"
}
```

### Webhook payload example

```json
{
  "eventId": "EVT-123456789",
  "eventType": "CLAIM_ACCEPTED",
  "timestamp": "2025-04-23T11:30:15Z",
  "data": {
    "claimId": "CLM-20250422-12345",
    "policyNumber": "POL-1234567890",
    "customerId": "CUS-98765",
    "status": "ACCEPTED",
    "paymentId": "PAY-001",
    "approvedAmount": {
      "value": 4500.00,
      "currency": "USD"
    }
  },
  "signature": "HMAC-SHA256-HEX-SIGNATURE"
}
```

## API Versioning

The API uses a URL versioning strategy (`/v1`). When new incompatible versions are released, the version number will be incremented (`/v2`, `/v3`, etc.).

## Error Responses

All error responses follow this structure:

```json
{
  "timestamp": "2025-04-22T10:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "details": [
    {
      "field": "incidentDate",
      "error": "Incident date cannot be in the future"
    },
    {
      "field": "damageAmount",
      "error": "Damage amount must be greater than zero"
    }
  ],
  "path": "/claims",
  "traceId": "5f7f1e3d2a8b9c0e7d6f5a4b"
}
```

## OpenAPI Specification

The complete OpenAPI 3.0 specification for this API is available at:
```
https://api.insurance-company.com/claims/v1/openapi.json
```

## Rate Limiting

API endpoints are subject to rate limiting to ensure system stability. Limits vary by endpoint and client type. Rate limit information is returned in response headers:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1714241730
```

## Environments

| Environment | Base URL |
|-------------|----------|
| Development | `https://dev-api.insurance-company.com/claims/v1` |
| Testing | `https://test-api.insurance-company.com/claims/v1` |
| Staging | `https://staging-api.insurance-company.com/claims/v1` |
| Production | `https://api.insurance-company.com/claims/v1` |