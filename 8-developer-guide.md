# Developer Guide - Insurance Claim Processing System

This guide provides essential information for developers working on the Insurance Claim Processing system, covering setup, standards, processes, and best practices to ensure consistent, high-quality development.

## 1. Development Environment Setup

### Prerequisites

- **JDK**: Amazon Corretto 21 or OpenJDK 21
- **Node.js**: v20.x LTS
- **Docker**: Latest stable version
- **Kubernetes**: minikube for local development
- **IDE**: IntelliJ IDEA Ultimate (recommended) or VS Code with Java extensions
- **API Testing**: Postman or Insomnia

### Local Setup Instructions

1. **Clone the repositories**:
   ```bash
   # Core services
   git clone https://github.com/insurance-company/claim-service.git
   git clone https://github.com/insurance-company/payment-service.git
   git clone https://github.com/insurance-company/document-service.git
   git clone https://github.com/insurance-company/notification-service.git
   
   # Frontend applications  
   git clone https://github.com/insurance-company/customer-portal.git
   git clone https://github.com/insurance-company/admin-portal.git
   git clone https://github.com/insurance-company/claims-department-portal.git
   ```

2. **Backend service setup**:
   ```bash
   cd claim-service
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. **Frontend setup**:
   ```bash
   cd customer-portal
   npm install
   npm run dev
   ```

4. **Local infrastructure**:
   ```bash
   # Start local infrastructure using Docker Compose
   cd deployment/local
   docker-compose up -d
   ```

### Using the Dev Environment

- Local services will be available at:
  - Customer Portal: http://localhost:3000
  - Admin Portal: http://localhost:3001
  - Claims Department Portal: http://localhost:3002
  - API Gateway: http://localhost:8080
  - Service endpoints: http://localhost:808x (where x is the service number)
  - MongoDB Express: http://localhost:8081
  - MinIO Console: http://localhost:9001

## 2. Coding Standards

### General Guidelines

- **Domain-Driven Design**: Adhere to DDD principles as outlined in our domain model documents
- **Clean Code**: Follow SOLID principles and clean code practices
- **Error Handling**: Use domain exceptions for business rule violations
- **Logging**: Follow the structured logging guidelines in Section 4
- **Comments**: Document "why" not "what" and keep documentation up-to-date

### Java Coding Standards

- Use [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Configuration file available at `.idea/codeStyles/`
- Additional rules:
  - Value objects must be immutable
  - Entities should encapsulate business logic
  - Commands should be immutable
  - Use Optional<T> for potentially missing values
  - Prefer Java Time API for date/time operations

### TypeScript/React Coding Standards

- Use [Airbnb React/JSX Style Guide](https://github.com/airbnb/javascript/tree/master/react)
- ESLint and Prettier configurations available in each frontend repo
- Additional rules:
  - Use functional components and hooks
  - Use TypeScript interfaces for props
  - Avoid any type
  - Use CSS modules or styled-components
  - Implement responsive design with MUI breakpoints

### Commit Message Standards

Follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code changes that neither fix bugs nor add features
- `perf`: Performance improvements
- `test`: Test additions or corrections
- `chore`: Changes to the build process or auxiliary tools

## 3. Architecture Compliance

To ensure the implementation adheres to our DDD architecture:

### Bounded Contexts

- Maintain clear boundaries between different bounded contexts
- Use context mapping patterns (Anti-Corruption Layer, Shared Kernel, etc.) as defined in the architecture
- Document any deviations or refinements to the context map

### Aggregate Guidelines

- Keep aggregates small and focused on single responsibility
- Ensure aggregate consistency (all invariants must be satisfied)
- Reference other aggregates by ID, not by direct object reference
- Validate commands at the aggregate level

### CQRS Implementation

- Keep command and query responsibilities strictly separated
- Commands must be processed by a single aggregate
- Queries should use optimized read models
- Use event sourcing for audit trails and state reconstruction

### Enforcing Architecture

- ArchUnit tests will validate architectural rules
- Regular architecture reviews during sprint retrospectives
- Architecture decision records (ADRs) for significant changes

## 4. Testing Strategy

### Test Pyramid

Follow the test pyramid approach with automated tests at all levels:

```
    /\
   /  \     E2E Tests (5%)
  /----\
 /      \   Integration Tests (15%)
/--------\
|        |  Unit Tests (80%)
|________|
```

### Required Test Coverage

- **Unit Tests**: Minimum 80% coverage, focus on business logic
- **Integration Tests**: Cover all service interfaces and external dependencies
- **E2E Tests**: Cover critical user journeys end-to-end
- **Performance Tests**: Response time, throughput, resource utilization
- **Security Tests**: OWASP Top 10 checks, penetration testing

### Testing Guidelines

- **Unit Tests**:
  - Use JUnit 5 and AssertJ for backend tests
  - Use Jest and React Testing Library for frontend tests
  - Mock external dependencies
  - Focus on behavior, not implementation details

- **Integration Tests**:
  - Use Testcontainers for database integration tests
  - Mock external services with WireMock
  - Test API contracts with Spring Cloud Contract
  - Include service-to-service communication tests

- **E2E Tests**:
  - Use Cypress for frontend E2E tests
  - Run against a staging environment
  - Test complete user journeys

- **Test Data Management**:
  - Use factories or builders for test data
  - Maintain test database schema in sync with production
  - Clean up test data after tests run

## 5. Logging and Monitoring

### Structured Logging

All logs should be in structured JSON format with the following fields:

```json
{
  "timestamp": "2025-04-22T10:15:30.123Z",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.insurance.claims.api.ClaimController",
  "message": "Claim submitted successfully",
  "context": {
    "requestId": "5f7f1e3d2a8b9c0e7d6f5a4b",
    "userId": "user-123",
    "claimId": "claim-456"
  },
  "exception": null
}
```

### Log Levels

- **ERROR**: Application errors requiring immediate attention
- **WARN**: Potential issues that don't stop the application
- **INFO**: Key business events and application lifecycle events
- **DEBUG**: Detailed information for troubleshooting (disabled in production)
- **TRACE**: Very detailed debugging (never in production)

### What to Log

- All domain events
- API requests and responses (excluding sensitive data)
- Authentication and authorization decisions
- Performance metrics for key operations
- Service startup and shutdown
- Error conditions with stack traces

### Monitoring

- Use Prometheus for metrics collection
- Define alerts for SLO violations
- Use distributed tracing with OpenTelemetry
- Monitor all external service dependencies

## 6. Error Handling

### Exception Hierarchy

```
- ApplicationException (base)
  |- DomainException
     |- ValidationException
     |- BusinessRuleViolationException
     |- ResourceNotFoundException
  |- TechnicalException
     |- ExternalServiceException
     |- DataAccessException
     |- SecurityException
```

### Error Response Format

All API errors should return a consistent JSON format:

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
    }
  ],
  "path": "/claims",
  "traceId": "5f7f1e3d2a8b9c0e7d6f5a4b"
}
```

### Error Handling Guidelines

- Don't catch exceptions unless you can properly handle them
- Log exceptions at their source
- Translate technical exceptions to user-friendly messages
- Include enough context to troubleshoot
- Don't expose sensitive information in error responses
- Validate input early

## 7. Security Guidelines

### Authentication

- Use OAuth 2.0 with OpenID Connect for authentication
- Implement multi-factor authentication for admin users
- Use JWT tokens with short expiration times
- Implement token refresh mechanism

### Authorization

- Use role-based access control (RBAC)
- Implement fine-grained permissions
- Validate authorization at the API gateway and service level
- Log all access control decisions

### Data Protection

- Encrypt sensitive data at rest and in transit
- Hash passwords with strong algorithms (bcrypt)
- Mask sensitive data in logs
- Implement data retention policies

### Secure Coding Practices

- Validate all inputs
- Use parameterized queries
- Sanitize outputs to prevent XSS
- Apply content security policy
- Implement rate limiting
- Use security headers (HSTS, X-Content-Type-Options, etc.)

## 8. Definition of Done

A task is considered "Done" when:

1. **Code Complete**:
   - Feature implemented according to requirements
   - Coding standards followed
   - Technical documentation updated

2. **Testing**:
   - Unit tests written and passing
   - Integration tests written and passing
   - E2E tests updated and passing
   - Test coverage meets targets

3. **Code Quality**:
   - Code review completed
   - Static analysis issues addressed
   - No new technical debt (or documented if necessary)

4. **Non-functional Requirements**:
   - Performance criteria met
   - Security requirements satisfied
   - Accessibility compliance verified

5. **Deployment Ready**:
   - Feature can be deployed independently
   - Feature can be turned on/off via feature flag
   - Monitoring and alerts configured

6. **Documentation**:
   - User documentation updated
   - API documentation updated
   - Architecture decision records updated if needed

## 9. Workflows and Processes

### Git Workflow

Follow the GitHub Flow model:

1. Create a feature branch from `main`
2. Make changes with regular commits
3. Push branch and create a pull request
4. Run automated checks
5. Code review
6. Merge to `main` after approval
7. Automated deployment to development environment

### Issue Management

- Use GitHub Issues for task tracking
- Follow the issue template
- Link issues to pull requests
- Use labels for categorization
- Use milestones for sprint planning

### Code Review Process

- All code must be reviewed by at least one other developer
- Use pull request templates
- Focus reviews on:
  - Business logic correctness
  - Architecture compliance
  - Security considerations
  - Test coverage
  - Performance implications
  - Code readability

## 10. Continuous Integration/Continuous Deployment

### CI Pipeline

The CI pipeline automatically runs on each pull request and push to `main`:

1. Compile and build
2. Run unit tests
3. Run integration tests
4. Static code analysis (SonarQube)
5. Dependency vulnerability scan
6. Build and tag Docker images
7. Publish artifacts

### CD Pipeline

The CD pipeline automatically deploys to environments:

1. Deploy to development environment (automatic)
2. Run smoke tests
3. Deploy to testing environment (automatic)
4. Run E2E tests
5. Deploy to staging environment (manual approval)
6. Run performance tests
7. Deploy to production environment (manual approval)

### Feature Flags

Use feature flags to:
- Deploy features that are not yet ready
- A/B test features
- Gradually roll out features
- Quickly disable problematic features

## 11. Resources and References

### Internal Documentation

- [Domain Model Documentation](./3-domain-model.md)
- [API Specifications](./6-api-specifications.md)
- [Implementation Architecture](./7-implementation-architecture.md)
- [Team Wiki](https://wiki.insurance-company.com/claims-processing)

### External References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://reactjs.org/docs/getting-started.html)
- [Domain-Driven Design Reference](https://www.domainlanguage.com/ddd/reference/)
- [12 Factor App Methodology](https://12factor.net/)
- [OWASP Security Practices](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)

## 12. Support and Communication

- **Technical Support**: #tech-support channel on Slack
- **Architecture Team**: #architecture channel on Slack
- **Daily Standups**: 10:00 AM ET via Teams
- **Architecture Office Hours**: Wednesdays 2:00-4:00 PM ET
- **On-call Rotation**: PagerDuty schedule in #oncall-schedule