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
| Backend | Kotlin, Kotlin Coroutines, Spring Boot (WebFlux) |
| Frontend | Angular, TypeScript, PrimeNG |
| Database | PostgreSQL, Spring Data R2DBC |
| Testing | JUnit 5, MockK, Coroutines Test |
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

**User Profile Service**
- Owns application-level user profiles.
- Stores display data, preferences, consents, and avatar metadata.
- Uses the immutable Keycloak `JWT.sub` claim as the user reference.
- Does not store passwords, refresh tokens, or access tokens.
- Is planned as a follow-up service after the Keycloak integration.

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

### 5.3 Service Internal Architecture

Microservices follow **Hexagonal Architecture (Ports and Adapters)**:
- `domain`: Pure business models, lifecycle transitions, invariants, and repository ports (interfaces).
- `application`: Use cases (inbound ports), commands, and application services orchestrating domain logic.
- `infrastructure`: Inbound adapters (REST controllers with Spring WebFlux, DTOs, global exception handling) and outbound adapters (Spring Data R2DBC repositories, messaging publishers).
- Non-blocking, reactive execution with Kotlin Coroutines (`suspend`, `Flow`).

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

The current local implementation uses the `eventhub` realm and the OpenID Connect
Authorization Code flow with PKCE for the Angular frontend. The event service validates
JWT access tokens as an OAuth2 Resource Server, maps Keycloak realm roles to Spring Security
roles, and derives the current user ID from the JWT `sub` claim. Organizer ownership is
checked in the event application service rather than trusted from the request body.

The current local Keycloak setup is intended for development only and uses its own
dedicated PostgreSQL database and persistent Docker volume, separate from all
business-service databases. Local realm users are imported from the repository
seed into that database on first startup. Keycloak database credentials,
backups, persistent storage, and deployment configuration must be managed as
infrastructure concerns. Application services must not access this database
directly.
The local realm seed keeps only authentication identity and realm roles;
display names and preferences remain future User Profile Service data.

Keycloak remains responsible for identity infrastructure, while the planned User Profile
Service will own application-level profile data. Profile data will be addressed by the
opaque Keycloak subject ID and stored in a separate database. Business services must not
depend on a synchronous profile lookup for their core write workflows.

Vault is intentionally separate from authentication. It is planned for storing application
secrets such as database and broker credentials.

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
- Implement the initial User Profile Service after authentication is verified.
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

**Status: Stage 1 & Stage 2 in progress – Event Service & Frontend UI implemented; initial CI/CD automation is in place.**

- `event-service`:
  - Core domain models (`Event`, `EventRegistration`) with state transition logic.
  - Hexagonal architecture with inbound ports (`EventUseCase`, `EventRegistrationUseCase`), separate command classes (`CreateEventCommand`, `RegisterAttendeeCommand`, `CancelRegistrationCommand`), and application services (`EventService`, `EventRegistrationService`).
  - Outbound persistence ports (`EventRepositoryPort`, `EventRegistrationRepositoryPort`) and reactive PostgreSQL adapters using Spring Data R2DBC.
  - REST APIs with Spring WebFlux (`/api/v1/events`, `/api/v1/events/{id}/registrations`), RFC 7807 error handling, and bean validation.
  - Production & Kubernetes readiness configuration (graceful shutdown, liveness/readiness probes, connection pool).
  - Unit, domain, service, and WebTestClient test suite implemented using JUnit 5, Kotlin Coroutines Test, and MockK.
  - Keycloak JWT resource-server integration with `USER`, `ORGANIZER`, and `ADMIN` roles.
  - Authenticated attendee registration endpoints derive the user ID from the JWT.
  - Local Keycloak runs against a dedicated PostgreSQL database with a persistent
    Docker volume and eight seeded presentation users.
- `frontend`:
  - Angular 18 standalone components styled with PrimeNG 18 and PrimeIcons (Aura preset).
  - Decomposed components: navbar, event list, event creation, event detail, attendee registrations (`EventRegistrationsComponent`), and event info card (`EventInfoCardComponent`).
  - Typed API service (`EventService`) integrated with reactive REST endpoints.
  - Local development proxy (`proxy.conf.json`) forwarding `/api` calls to `http://localhost:8081` without requiring CORS on backend services.
  - Karma/Jasmine unit tests with headless Chrome execution.

- CI/CD:
  - GitHub Actions validates pull requests and pushes to `main` with backend tests, frontend build/tests, and Docker Compose configuration validation.
  - Version tags matching `v*` build and publish the Event Service image to GitHub Container Registry.
  - Frontend dependency lockfile resolves packages from the public npm registry so GitHub-hosted runners can install dependencies without private network access.

- Local development data:
  - Event Service PostgreSQL initialization includes deterministic demo events in `PUBLISHED`, `DRAFT`, and `CANCELLED` states, plus eight registrations linked to opaque Keycloak subject IDs.
  - Keycloak uses a separate PostgreSQL database and imports eight fictional demo users from `infrastructure/keycloak/eventhub-realm.json`.
  - The demo dataset and realm seed are created only for fresh volumes; use `docker compose down -v && docker compose up -d` to recreate them.

- Next steps:
  - Implement the User Profile Service as a separate vertical slice.
  - Add an outbox and integrate RabbitMQ (publishing business events like `EventPublished`, `EventRegistered`, `EventRegistrationCancelled`).
  - Volunteer Service implementation.

This document should be updated when important architectural or functional decisions change.

## 13. Documentation Map

Additional documentation can be introduced as the project grows:

- `docs/architecture/` – architecture diagrams and technical decisions.
- `docs/api/` – API contracts and OpenAPI specifications.
- `docs/database/` – data models and database decisions.
- `docs/messaging/` – RabbitMQ and Kafka event definitions.
- `docs/deployment/` – Docker and Kubernetes deployment instructions.

Avoid duplicating information between documents. Keep this file as the high-level source of project context.
