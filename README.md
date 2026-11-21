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
- **User Profile Service** – manages the authenticated user's application-level profile.
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
├── profile-service/
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
This starts the infrastructure containers, including the Event Service
PostgreSQL database, the User Profile Service PostgreSQL database, the
dedicated Keycloak PostgreSQL database, and Keycloak.
On the initial startup, PostgreSQL automatically executes the Event Service
initialization scripts located in `infrastructure/postgres/init/` to create the
required tables and indexes. Keycloak imports the local `eventhub` realm and
demo users from `infrastructure/keycloak/eventhub-realm.json` into its own
database.

For local authentication details and demo users, see [Keycloak setup](docs/security/KEYCLOAK.md).

> **Tip:** If you want to run all services inside Docker instead of running them locally:
> ```bash
> docker compose --profile apps up -d --build
> ```

> **Tip:** If you need to reset the database and re-run initialization scripts from scratch:
> ```bash
> docker compose down -v && docker compose up -d
> ```

## Local demo data

A fresh Event Service PostgreSQL volume is initialized with four demo events:
two published events, one draft event, and one cancelled event. The dataset
also includes eight registration rows linked to the fixed Keycloak subject IDs
of the demo users. The login accounts and passwords are documented in
[Keycloak setup](docs/security/KEYCLOAK.md).

The Keycloak realm seed is imported into the separate `keycloak_pgdata` volume
only when the realm does not already exist. To recreate both databases, users,
and demo events from scratch:

```bash
docker compose down -v
docker compose up -d
```

The local Keycloak database is exposed on host port `5433`; the Event Service
database remains on `5432`. Set `KEYCLOAK_DB_PASSWORD` and
`KEYCLOAK_ADMIN_PASSWORD` in the environment to override the development
defaults. These accounts and passwords are fictional and must not be reused
outside local development.

After that, start the Event Service, User Profile Service, and frontend as
described below, then log in with one of the demo users. Use any organizer account to create or manage
events and `user` (or another attendee) to register and cancel a registration.

### Demo Keycloak accounts

| Username | Password | Roles |
|---|---|---|
| `ada.lovelace` | `ada` | `USER`, `ORGANIZER` |
| `linus.torvalds` | `linus` | `USER`, `ORGANIZER` |
| `guido.vanrossum` | `guido` | `USER`, `ORGANIZER` |
| `grace.hopper` | `grace` | `USER`, `ORGANIZER` |
| `margaret.hamilton` | `margaret` | `USER`, `ORGANIZER` |
| `user` | `user` | `USER` |
| `anna.nowak` | `anna` | `USER` |
| `piotr.kowalski` | `piotr` | `USER` |
| `maria.zielinska` | `maria` | `USER` |
| `pawel.lewandowski` | `pawel` | `USER` |
| `karolina.wojcik` | `karolina` | `USER` |

These are local presentation accounts only. Keycloak stores them in the
dedicated PostgreSQL database after the first realm import.

### 2. Run Event Service Locally
```bash
./event-service/gradlew -p event-service bootRun
```
The service connects to PostgreSQL on `localhost:5432` by default using credentials specified in `application.yaml` and `docker-compose.yml`.

### 3. Run User Profile Service Locally
```bash
./profile-service/gradlew -p profile-service bootRun
```
The service connects to its separate PostgreSQL database on `localhost:5434`.
Flyway applies the profile schema migrations on startup.

## CI/CD

GitHub Actions validates every pull request and push to `main`:

- backend tests run with Java 25 and the Gradle wrapper;
- frontend dependencies are installed with `npm ci`, then Angular production build and headless browser tests run with Node.js 20;
- Docker Compose configuration is validated with `docker compose config --quiet`.

A version tag such as `v1.0.0` starts the release workflow. It builds both service
Dockerfiles and publishes the images to GitHub Container Registry as:

```text
ghcr.io/igor-shishkin/eventhub-event-service:v1.0.0
ghcr.io/igor-shishkin/eventhub-event-service:latest
ghcr.io/igor-shishkin/eventhub-profile-service:v1.0.0
ghcr.io/igor-shishkin/eventhub-profile-service:latest
```

### 4. Run Frontend Locally
```bash
cd frontend
npm install
npm start
```
The application will be accessible at `http://localhost:4200`. API requests to `/api` are automatically proxied to `http://localhost:8081` via `proxy.conf.json`.

## Development Status

**Current stage:** Stage 2 in progress – Event Service, User Profile Service, and Frontend UI implemented.

The project is being developed incrementally.

Planned development stages:

1. Project setup and frontend development.
2. Core backend microservices and REST APIs.
3. Authentication and authorization with Keycloak, including local PostgreSQL persistence. **Implemented.**
4. Initial User Profile Service with `GET/PUT /api/v1/me`. **Implemented.**
5. Asynchronous business communication using RabbitMQ.
6. Event streaming and analytics using Apache Kafka.
7. Docker containerization and Kubernetes deployment.
8. Monitoring, logging, and distributed tracing.
9. CI/CD and infrastructure automation.

Features and infrastructure components described as planned are not necessarily implemented yet.

## Documentation

- [Project Context](docs/PROJECT_CONTEXT.md) – overall project goals, architecture, features, and technical decisions.
- [User Profile Service](docs/architecture/USER_PROFILE_SERVICE.md) – profile ownership boundaries and current API.
- [Copilot Instructions](.github/copilot-instructions.md) – guidelines for AI-assisted development.

## License

To be defined.
