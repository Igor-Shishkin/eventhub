# EventHub Agent Guide

## Project purpose

EventHub is a learning project for event management, microservices, asynchronous
messaging, and cloud-native operations. Prefer small, independently reviewable
vertical slices over broad rewrites.

## Architecture rules

- Each service owns its data and database schema.
- Services must not access another service's database directly.
- Keycloak owns authentication and identity infrastructure:
  passwords, sessions, password resets, identity-provider integrations, and
  realm roles.
- Use the validated JWT `sub` claim as the opaque user identifier.
- The planned User Profile Service owns application-level profile data, but
  never passwords, refresh tokens, or access tokens.
- RabbitMQ is for business workflows and asynchronous service communication.
- Kafka is primarily for user-activity events and analytics.
- Core business writes must not depend on a synchronous Profile Service lookup.

## Keycloak and persistence

The current local Keycloak setup is development-only and uses `start-dev`.
It does not yet have a dedicated PostgreSQL database. A planned infrastructure
step is to add a separate PostgreSQL database owned by Keycloak, with persistent
storage, backups, and secrets supplied outside the repository.

Do not add passwords or production secrets to source control. The local demo
credentials documented in `docs/security/KEYCLOAK.md` are for development only.

## Local development

Useful commands:

```bash
docker compose up -d postgres-event keycloak
./event-service/gradlew -p event-service test
cd frontend && npm test -- --watch=false
cd frontend && npm run build
```

Read the relevant documentation before changing authentication or service
boundaries:

- `docs/PROJECT_CONTEXT.md`
- `docs/security/KEYCLOAK.md`
- `docs/architecture/USER_PROFILE_SERVICE.md`
- `docs/architecture/DECISIONS.md`

## Change guidelines

- Keep changes focused on one feature or architectural step.
- Update documentation when changing a boundary, data owner, message contract,
  or infrastructure assumption.
- Add or update tests with implementation changes.
- Do not trust frontend route visibility for authorization; enforce access in
  the backend.
- Do not accept the current user ID or organizer ID from request bodies when it
  can be derived from the JWT.
- Before proposing a PR, run the relevant backend/frontend tests and
  `git diff --check`.

