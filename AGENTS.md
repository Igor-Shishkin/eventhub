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

The local Keycloak setup is development-only and uses `start-dev` with a
dedicated `postgres-keycloak` PostgreSQL service and `keycloak_pgdata` volume.
The Event Service database and Keycloak database are separate ownership
boundaries. Keycloak imports the local realm and demo users from
`infrastructure/keycloak/eventhub-realm.json` into its database on first
startup.

Do not add production passwords or secrets to source control. The local
development defaults and demo credentials documented in
`docs/security/KEYCLOAK.md` are intentionally for local use only. Override
`KEYCLOAK_DB_PASSWORD` and `KEYCLOAK_ADMIN_PASSWORD` through the environment
when needed.

## Local development

Useful commands:

```bash
docker compose up -d postgres-event postgres-keycloak keycloak
./event-service/gradlew -p event-service test
cd frontend && npm test -- --watch=false
cd frontend && npm run build
```

The Keycloak realm seed is applied only when the Keycloak database is empty.
After changing demo users or roles in the realm JSON, reset both local
databases with `docker compose down -v` before starting the stack again.

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
