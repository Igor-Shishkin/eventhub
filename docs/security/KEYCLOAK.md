# Local authentication with Keycloak

EventHub uses Keycloak as the identity provider. The Angular application authenticates with
the OpenID Connect Authorization Code flow with PKCE. The backend validates the resulting
JWT access token as an OAuth2 Resource Server.

## Start locally

```bash
docker compose up -d postgres-event keycloak
./event-service/gradlew -p event-service bootRun
cd frontend && npm start
```

Open `http://localhost:4200` and use the **Login** button.

The local realm is imported from `infrastructure/keycloak/eventhub-realm.json`.

Demo users:

| Username | Password | Roles |
|---|---|---|
| `organizer` | `organizer` | `USER`, `ORGANIZER` |
| `user` | `user` | `USER` |

The Keycloak administration console is available at `http://localhost:8080`.
The development-only administrator credentials are `admin` / `admin`.

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

Override it with `KEYCLOAK_ISSUER_URI` when the service runs in a different network.

## Persistence and next steps

The current local setup runs Keycloak in development mode without a dedicated
database. This is suitable only for local experiments; the Keycloak container
may lose its users and configuration when its local data is removed.

Before using Keycloak in a shared or production-like environment, add a
dedicated PostgreSQL database owned by Keycloak:

```text
Keycloak → PostgreSQL (Keycloak database)
```

This database is separate from the Event Service and any future User Profile
Service database. Application services must not access Keycloak's database
directly. They should use OIDC/JWT validation and Keycloak's public APIs where
needed.

The planned setup should include:

- a separate PostgreSQL database and credentials for Keycloak;
- persistent storage and backups for the Keycloak database;
- secrets supplied through environment configuration or Vault rather than
  committed to the repository;
- a production-oriented Keycloak deployment mode instead of `start-dev`;
- migration and realm-import procedures that are safe to repeat.

Keycloak remains responsible for passwords, sessions, password resets, and
identity-provider integrations. The planned User Profile Service will store
application-level profile data only and must never store passwords or tokens.

This setup does not use Vault. Vault will be introduced separately for application secrets
such as database and RabbitMQ credentials.
