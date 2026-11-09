# EventHub

EventHub is a platform for discovering and organizing events, managing attendees, and coordinating volunteers.

The goal of the project is to build a scalable application while exploring modern backend development, microservices architecture, event-driven systems, and DevOps practices.

## Main Features

- Browse and discover local and nationwide events.
- View event details, schedules, and locations.
- Register for free events.
- Create and manage events.
- Define volunteer positions and shifts.
- Collect volunteer availability.
- Automatically assign volunteers to shifts.
- Manage volunteer schedules.
- Collect user activity events for analytics.
- Generate statistics and insights about events and user activity.

## Technology Stack

| Area | Technology |
|---|---|
| Backend | Kotlin, Spring Boot |
| Frontend | Angular, TypeScript, PrimeNG |
| Database | PostgreSQL |
| Business Messaging | RabbitMQ |
| Event Streaming & Analytics | Apache Kafka |
| Authentication & Authorization | Keycloak |
| API Gateway | Spring Cloud Gateway (planned) |
| Containers | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Infrastructure as Code | Terraform (planned) |
| Secrets Management | HashiCorp Vault (planned) |
| Monitoring | Prometheus, Grafana (planned) |
| Logging | Loki (planned) |
| Distributed Tracing | OpenTelemetry (planned) |

## Architecture

The application is designed as a microservices-based system using event-driven communication.

### Planned Services

- **Event Service** – manages events, schedules, and attendee registrations.
- **Volunteer Service** – manages volunteer positions, availability, shifts, and assignments.
- **Notification Service** – handles email notifications and reminders.
- **Analytics Service** – consumes user activity events and generates statistics and insights.
- **API Gateway** – provides a single entry point to backend services.
- **Keycloak** – handles authentication and authorization.

### Messaging & Event Streaming

The project uses two messaging technologies for different purposes:

**RabbitMQ – Business Communication**
- Asynchronous communication between microservices.
- Event-driven business workflows.
- Notification processing.
- Volunteer assignment workflows.

**Apache Kafka – Event Streaming & Analytics**
- Collecting user activity events.
- Tracking event views, registrations, and cancellations.
- Processing event streams.
- Feeding analytics services and future data warehouse integrations.

Example Kafka events:
- `EventViewed`
- `EventRegistered`
- `EventRegistrationCancelled`
- `VolunteerAvailabilitySubmitted`
- `VolunteerAssignmentAccepted`

### Data Ownership

- Each microservice owns its data.
- Services do not directly access another service's database.
- PostgreSQL is used for persistent business data.
- RabbitMQ and Kafka are used for asynchronous communication and event streaming.

## Project Structure

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

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 25 (or version matching the Gradle Toolchain configuration)

### 1. Start Infrastructure (Databases, etc.)
```bash
docker compose up -d
```
This starts only the infrastructure containers (PostgreSQL). On the initial startup, PostgreSQL automatically executes the initialization scripts located in `infrastructure/postgres/init/` to create the required tables and indexes.

> **Tip:** If you want to run all services inside Docker instead of running them locally:
> ```bash
> docker compose --profile apps up -d --build
> ```

> **Tip:** If you need to reset the database and re-run initialization scripts from scratch:
> ```bash
> docker compose down -v && docker compose up -d
> ```

### 2. Run Event Service Locally
```bash
./event-service/gradlew -p event-service bootRun
```
The service connects to PostgreSQL on `localhost:5432` by default using credentials specified in `application.yaml` and `docker-compose.yml`.

### 3. Run Frontend Locally
```bash
cd frontend
npm install
npm start
```
The application will be accessible at `http://localhost:4200`. API requests to `/api` are automatically proxied to `http://localhost:8081` via `proxy.conf.json`.

## Development Status

**Current stage:** Stage 1 & 2 in progress – Event Service REST API & Frontend UI implemented.

The project is being developed incrementally.

Planned development stages:

1. Project setup and frontend development.
2. Core backend microservices and REST APIs.
3. Authentication and authorization with Keycloak.
4. Asynchronous business communication using RabbitMQ.
5. Event streaming and analytics using Apache Kafka.
6. Docker containerization and Kubernetes deployment.
7. Monitoring, logging, and distributed tracing.
8. CI/CD and infrastructure automation.

Features and infrastructure components described as planned are not necessarily implemented yet.

## Documentation

- [Project Context](docs/PROJECT_CONTEXT.md) – overall project goals, architecture, features, and technical decisions.
- [Copilot Instructions](.github/copilot-instructions.md) – guidelines for AI-assisted development.

## License

To be defined.