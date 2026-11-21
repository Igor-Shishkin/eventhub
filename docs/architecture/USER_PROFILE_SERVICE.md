# User Profile Service

## Status

Initial vertical slice implemented. The service currently supports the authenticated
user's application-level profile and has its own PostgreSQL database and Flyway
migration history. Profile photos, public profiles, and profile events remain
follow-up work.

## Responsibility

The User Profile Service owns application-level user information:

- display name;
- short biography;
- locale and timezone;
- email notification preference;
- future avatar metadata and privacy preferences.

Keycloak remains the owner of authentication and identity infrastructure:

- passwords;
- login sessions;
- JWT access tokens;
- password reset;
- identity-provider integration;
- coarse-grained realm roles.

The profile service never stores passwords, refresh tokens, or access tokens.

## Identity reference

Every profile is identified by the immutable Keycloak subject claim:

```text
profile.userId = JWT.sub
```

The user ID is treated as an opaque string. Email addresses and usernames are not
used as primary keys because they may change. The current user is always derived
from the validated JWT; clients cannot choose the profile owner.

## Initial API

```text
GET /api/v1/me
PUT /api/v1/me
```

`GET /api/v1/me` lazily creates a minimal profile when the authenticated user has
not used the service before. `PUT /api/v1/me` updates only the profile belonging
to the current JWT subject.

The initial response contains:

- `userId`;
- `displayName`;
- `bio`;
- `locale`;
- `timezone`;
- `emailNotificationsEnabled`;
- `createdAt` and `updatedAt`.

The `userId` is returned only as the current user's opaque identity. It is not a
public profile identifier and must not be displayed as a user-facing name.

## Data ownership

The service owns the `user_profiles` table in the separate `profile_service`
database. Flyway applies migrations from `profile-service/src/main/resources/db/migration`.
Other services may store the opaque `userId` reference but must not access this
database directly.

User creation is intentionally not replicated into every service database.
Keycloak owns the identity record; the Profile Service owns the application
profile. Until registration events are introduced, the service uses lazy profile
creation on the first authenticated profile request.

The Event Service must not synchronously call the Profile Service during event
registration. Registration remains available when profile reads are unavailable.
If event attendee lists later need display data, use API composition, a local
read model, or a deliberately designed public-profile endpoint.

## Local development

Docker Compose exposes the profile database on host port `5434` and the profile
service on `8082`:

```bash
docker compose up -d postgres-profile keycloak
./profile-service/gradlew -p profile-service bootRun
```

The Angular development proxy sends `/api/v1/me` to the profile service and other
`/api` requests to the Event Service.

### Local demo-data seeding (planned next step)

Demo data must not be loaded by a plain `docker compose up`. The planned
approach is a one-shot `demo-seeder` container enabled only through an explicit
Compose profile (this is a Compose activation label, not the Profile Service),
for example:

```bash
docker compose --profile seed-data up demo-seeder
```

The seeder should orchestrate separate, idempotent seed scripts for the Event
Service and Profile Service. It must wait until the relevant databases and
service migrations are ready, then insert only local demo data. It must not
write directly to the Keycloak database; Keycloak continues to import the
development realm and users through `infrastructure/keycloak/eventhub-realm.json`.

Suggested layout:

```text
infrastructure/seed/
├── event-service/
│   └── 01-demo-events.sql
├── profile-service/
│   └── 01-demo-profiles.sql
└── seed.sh
```

The Profile Service schema remains owned by the service and must continue to be
created by Flyway. The seeder adds data only after the migrations have run.

## Follow-up implementation order

1. Verify or add a clearly visible `Logout` button for authenticated users in the
   frontend navbar and connect it to the Keycloak logout flow.
2. Add the explicit `seed` Compose profile and one-shot `demo-seeder`.
3. Add public profile summaries and explicit profile/attendance visibility settings.
4. Add avatar metadata and MinIO-backed photo upload/delete.
5. Add a `UserRegistered` RabbitMQ event and an idempotent Profile Service
   consumer that creates a minimal profile. Keep lazy creation as a fallback
   for delayed or missed delivery. Keycloak will need an event bridge, listener
   SPI, or registration backend integration to publish the event.
6. Add profile integration events with idempotent consumers.
7. Add API composition or read models only when another product feature needs them.
