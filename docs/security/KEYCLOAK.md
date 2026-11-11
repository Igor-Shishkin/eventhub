# Local authentication with Keycloak

EventHub uses Keycloak as the identity provider. The Angular application authenticates with
the OpenID Connect Authorization Code flow with PKCE. The backend validates the resulting
JWT access token as an OAuth2 Resource Server.

## Start locally

```bash
docker compose up -d postgres-event postgres-keycloak keycloak
./event-service/gradlew -p event-service bootRun
cd frontend && npm start
```

Open `http://localhost:4200` and use the **Login** button.

The local realm is imported from `infrastructure/keycloak/eventhub-realm.json`.
The seed contains authentication data only: a fixed subject ID, username,
password credential, and realm roles. Display names and other application
profile data belong to the future User Profile Service.

Demo users:

| Username | Password | Roles | Use for |
|---|---|---|---|
| `ada.lovelace` | `ada` | `USER`, `ORGANIZER` | Create, publish, and cancel events |
| `linus.torvalds` | `linus` | `USER`, `ORGANIZER` | Manage the second demo organizer's events |
| `guido.vanrossum` | `guido` | `USER`, `ORGANIZER` | Create and manage demo events |
| `grace.hopper` | `grace` | `USER`, `ORGANIZER` | Create and manage demo events |
| `margaret.hamilton` | `margaret` | `USER`, `ORGANIZER` | Create and manage demo events |
| `user` | `user` | `USER` | Register and cancel a registration |
| `anna.nowak` | `anna` | `USER` | Demo attendee |
| `piotr.kowalski` | `piotr` | `USER` | Demo attendee |
| `maria.zielinska` | `maria` | `USER` | Demo attendee |
| `pawel.lewandowski` | `pawel` | `USER` | Demo attendee |
| `karolina.wojcik` | `karolina` | `USER` | Demo attendee |

All accounts are fictional local-demo users. Their fixed Keycloak subject IDs
are used by the Event Service's seeded organizer and registration rows, so
authorization and "my registration" flows work immediately after a clean
database start.

The realm JSON is intentional: it is Keycloak's supported realm-import format.
The Event Service uses SQL because it initializes its own PostgreSQL schema.
Do not replace the Keycloak import with direct SQL against Keycloak's internal
tables; those tables are Keycloak-owned and can change between versions.

The Keycloak administration console is available at `http://localhost:8080`.
The development-only administrator credentials are `admin` / `admin` by default.
Set `KEYCLOAK_ADMIN_PASSWORD` before `docker compose up` to use another local
password.

## Request behaviour

- Public event browsing does not require authentication.
- Creating, publishing, and cancelling events requires the `ORGANIZER` role.
- An event's organizer is taken from the JWT `sub` claim; it is not accepted from the request body.
- Registering for an event requires authentication.
- A user can read or cancel only their own registration through the `/registrations/me` endpoints.
- Organizer access to the attendee list is enforced by the backend.

Angular route visibility is only a user-experience improvement. The backend remains the
source of truth for authorization.

## Configuration

The backend uses this issuer by default:

```text
http://localhost:8080/realms/eventhub
```

Override it with `KEYCLOAK_ISSUER_URI` when the service runs in a different
network.

## Persistence

Keycloak uses the separate `postgres-keycloak` service and the
`keycloak_pgdata` Docker volume:

```text
Keycloak → postgres-keycloak (keycloak database)
Event Service → postgres-event (event_service database)
```

This database is separate from the Event Service and any future User Profile
Service database. Application services must not access Keycloak's database
directly. They should use OIDC/JWT validation and Keycloak's public APIs where
needed.

The local Compose defaults are:

- database host port `5433` (container port `5432`);
- database name `keycloak`;
- database user `keycloak`;
- database password `keycloak_password`, unless `KEYCLOAK_DB_PASSWORD` is set.

The realm file is a bootstrap seed, not the runtime source of truth. Keycloak
imports it only when the realm does not already exist. To recreate the
database, users, and realm from the repository seed:

```bash
docker compose down -v
docker compose up -d postgres-event postgres-keycloak keycloak
```

The setup is still development-only: it uses `start-dev`, local default
credentials, and a local Docker volume. Shared or production-like deployments
must supply secrets, backups, persistent storage, and a production-oriented
Keycloak mode through infrastructure configuration.

Keycloak remains responsible for passwords, sessions, password resets, and
identity-provider integrations. The planned User Profile Service will store
application-level profile data only and must never store passwords or tokens.

This setup does not use Vault. Vault will be introduced separately for application secrets
such as database and RabbitMQ credentials.
