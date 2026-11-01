# EventHub – Project Context

## 1. Project Overview

EventHub is a platform for discovering and organizing local and nationwide events, managing attendees, and coordinating volunteers.

The platform aims to simplify event organization by providing tools for event discovery, registration, volunteer coordination, and activity analytics.

The project is also a practical learning environment for modern backend engineering, microservices architecture, event-driven systems, cloud infrastructure, and DevOps.

## 2. Main Goals

- Build a functional event management platform.
- Practice designing and implementing microservices.
- Learn asynchronous communication and event-driven architecture.
- Gain practical experience with RabbitMQ and Apache Kafka.
- Develop and deploy a containerized application using Docker and Kubernetes.
- Implement authentication and authorization.
- Build monitoring, logging, and distributed tracing.
- Practice CI/CD and infrastructure automation.

## 3. Main Functional Areas

### 3.1 Event Management

Organizers can:
- Create and edit events.
- Publish and manage events.
- Define event descriptions, locations, dates, and schedules.
- View attendee registrations.

Visitors can:
- Browse published events.
- Search for events by name and location.
- View event details.
- Register for free events.

**MVP scope:** Free events only. Paid registrations and payment processing are outside the initial scope.

### 3.2 Volunteer Management

Organizers can:
- Define volunteer positions and tasks.
- Specify required volunteer headcount.
- Define locations and shifts.
- Review volunteer availability.
- Generate volunteer assignments automatically.
- View staffing gaps.

Volunteers can:
- Submit their availability time windows.
- View assigned positions and schedules.
- Accept or review their assignments.

The initial assignment algorithm should consider:
- Volunteer availability.
- Required headcount.
- Position requirements and skills, when applicable.
- Overlapping shifts.
- Maximum volunteer working hours.

The initial implementation should use a deterministic algorithm rather than AI.

### 3.3 Notifications

The system should support:
- Registration confirmations.
- Volunteer assignment notifications.
- Event reminders.
- Important event updates.

Notifications will be processed asynchronously.

### 3.4 Analytics

The platform should collect selected user activity events and make them available for analytics.

Examples:
- Event viewed.
- Event registration completed.
- Event registration cancelled.
- Volunteer availability submitted.
- Volunteer assignment accepted.

Potential analytics:
- Most popular events.
- Event views and registrations.
- View-to-registration conversion.
- User activity statistics.
- Popular event categories and locations.

Analytics should not interfere with the main business workflows.

Only necessary event data should be collected. Avoid sending passwords, authentication tokens, or unnecessary personal information to analytics streams.

## 4. Technology Stack

| Area | Technology |
|---|---|
| Backend | Kotlin, Spring Boot |
| Frontend | Angular, TypeScript |
| Database | PostgreSQL |
| Business Messaging | RabbitMQ |
| Event Streaming | Apache Kafka |
| Authentication | Keycloak |
| API Gateway | Spring Cloud Gateway |
| Containers | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Infrastructure as Code | Terraform |
| Secrets Management | HashiCorp Vault |
| Monitoring | Prometheus, Grafana |
| Logging | Loki |
| Distributed Tracing | OpenTelemetry |

Technologies may be introduced incrementally. A technology being listed here does not mean it is already implemented.

## 5. High-Level Architecture

The system follows a microservices architecture.

### 5.1 Planned Services

**Event Service**
- Owns event information.
- Manages event schedules.
- Handles attendee registrations.
- Exposes event-related REST APIs.

**Volunteer Service**
- Manages volunteer positions and shifts.
- Stores volunteer availability.
- Generates volunteer assignments.
- Manages volunteer schedules.

**Notification Service**
- Processes notification requests.
- Sends emails and reminders.
- Consumes relevant business events.

**Analytics Service**
- Consumes activity events from Kafka.
- Processes user activity data.
- Generates statistics and analytics.

**API Gateway**
- Provides a single entry point for frontend requests.
- Routes requests to appropriate backend services.

**Keycloak**
- Manages users, authentication, and roles.
- Issues JWT access tokens.
- Supports role-based authorization.

### 5.2 Data Ownership

Each microservice owns its data.

Rules:
- A service must not directly access another service's database.
- Services communicate through APIs or messaging.
- PostgreSQL is used for persistent business data.
- Separate logical databases or schemas may be used during local development, but service ownership must remain clear.

## 6. Messaging Architecture

RabbitMQ and Kafka have separate responsibilities.

### 6.1 RabbitMQ – Business Communication

Used for:
- Asynchronous communication between microservices.
- Notification processing.
- Business workflows.
- Volunteer assignment-related tasks.

Example events:
- `EventPublished`
- `VolunteerScheduleGenerated`
- `NotificationRequested`

### 6.2 Kafka – Event Streaming and Analytics

Used for:
- Collecting user activity events.
- Streaming business-related activity data.
- Feeding analytics services.
- Future integrations with data warehouses.

Example events:
- `EventViewed`
- `EventRegistered`
- `EventRegistrationCancelled`
- `VolunteerAvailabilitySubmitted`
- `VolunteerAssignmentAccepted`

Kafka should primarily support analytics and activity processing, not duplicate every RabbitMQ workflow.

## 7. Authentication and Authorization

Keycloak is responsible for identity management.

Initial roles:
- `USER`
- `VOLUNTEER`
- `ORGANIZER`
- `ADMIN`

The frontend authenticates through Keycloak and sends JWT access tokens with API requests.

The API Gateway and backend services validate tokens and enforce authorization rules.

Business services should not depend on Keycloak being queried on every request. JWT signature validation using Keycloak's public keys is preferred.

## 8. Frontend

The frontend is built with Angular and TypeScript.

Initial functional areas:
- Event discovery.
- Event details.
- Event creation and editing.
- User registration and login.
- Attendee registrations.
- Volunteer availability.
- Volunteer schedule.
- Organizer dashboard.

The frontend should initially focus on a responsive web application. A mobile application may be considered later.

## 9. Development Principles

- Start with simple, working implementations.
- Develop incrementally, one feature at a time.
- Keep services independently deployable.
- Respect service data ownership.
- Use asynchronous messaging when it provides a clear benefit.
- Avoid unnecessary abstractions and premature optimization.
- Write tests for important business logic.
- Document significant architectural decisions.
- Do not introduce new technologies without a clear reason.

## 10. Project Structure

```text
eventhub/
├── .github/
│   └── copilot-instructions.md
├── docs/
│   └── PROJECT_CONTEXT.md
├── frontend/
├── event-service/
├── volunteer-service/
├── notification-service/
├── analytics-service/
├── infrastructure/
└── README.md
```

## 11. Development Roadmap

### Stage 1 – Project Foundation
- Create repository structure.
- Initialize Angular frontend.
- Create the first Spring Boot service.
- Define initial domain models and REST APIs.
- Implement the first frontend screens using mock data.

### Stage 2 – Core Backend
- Implement Event Service.
- Implement Volunteer Service.
- Integrate PostgreSQL.
- Connect frontend to backend APIs.
- Implement basic authentication with Keycloak.

### Stage 3 – Messaging
- Introduce RabbitMQ.
- Implement asynchronous business workflows.
- Introduce Kafka.
- Publish and consume user activity events.
- Implement the first Analytics Service.

### Stage 4 – Containerization and Kubernetes
- Create Dockerfiles.
- Configure Docker Compose for local development.
- Deploy services to Kubernetes.
- Configure Services, Ingress, ConfigMaps, and Secrets.
- Add health checks and resource limits.

### Stage 5 – Observability and Reliability
- Introduce Prometheus and Grafana.
- Configure centralized logging with Loki.
- Add distributed tracing with OpenTelemetry.
- Test service failures and recovery.
- Introduce backups and reliability improvements.

### Stage 6 – CI/CD and Infrastructure
- Implement GitHub Actions pipelines.
- Introduce Terraform.
- Introduce Vault for secrets management.
- Prepare separate development and production environments.

## 12. Current Project Status

**Status: Initial project setup.**

The repository has been created. The architecture and technology choices are being defined.

Most components described in this document are planned and have not yet been implemented.

This document should be updated when important architectural or functional decisions change.

## 13. Documentation Map

Additional documentation can be introduced as the project grows:

- `docs/architecture/` – architecture diagrams and technical decisions.
- `docs/api/` – API contracts and OpenAPI specifications.
- `docs/database/` – data models and database decisions.
- `docs/messaging/` – RabbitMQ and Kafka event definitions.
- `docs/deployment/` – Docker and Kubernetes deployment instructions.

Avoid duplicating information between documents. Keep this file as the high-level source of project context.