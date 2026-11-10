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

This setup does not use Vault. Vault will be introduced separately for application secrets
such as database and RabbitMQ credentials.
