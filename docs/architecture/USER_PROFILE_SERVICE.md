# User Profile Service

## Status

Planned. This service is intentionally separate from the current Keycloak
authentication work and will be implemented as a follow-up vertical slice.

## Responsibility

The User Profile Service owns application-level user information:

- display name;
- short biography;
- locale and timezone;
- avatar metadata;
- notification preferences;
- privacy and communication consents;
- optional profile information used by other domains.

Keycloak remains the owner of authentication and identity infrastructure:

- passwords;
- login sessions;
- JWT access tokens;
- password reset;
- identity-provider integration;
- coarse-grained realm roles.

The profile service must never store user passwords, refresh tokens, or access tokens.

## Identity reference

Every profile is identified by the immutable Keycloak subject claim:

```text
profile.userId = JWT.sub
```

The user ID is treated as an opaque string. Email addresses and usernames are not
used as primary keys because they may change.

The service derives the current user from the validated JWT. A client must not be
allowed to choose the `userId` of the profile being modified.

## Initial API

The first version should focus on the current user:

```text
GET  /api/v1/me
PUT  /api/v1/me
POST /api/v1/me/photo
DELETE /api/v1/me/photo
```

Administrative profile management can be added later if a real use case requires it.

## Data ownership

The service owns its own database and profile schema. Other services store only
the opaque `userId` reference.

The Event Service should not synchronously call the Profile Service during event
registration. Registration must remain available even if profile reads or image
storage are temporarily unavailable.

If another service needs display data, possible approaches are:

1. fetch the profile separately at the API composition layer;
2. maintain a small local read model from profile events;
3. store a deliberately named snapshot when the business operation requires one.

Cross-service database access is not allowed.

## Profile photo

The database should store photo metadata and an object key, not the image bytes:

```text
photoObjectKey
photoContentType
photoUpdatedAt
```

For local development, MinIO can provide S3-compatible object storage. Production
can use S3 or another compatible object store without changing the profile domain.

The first implementation may accept multipart uploads through the Profile Service.
Presigned upload URLs can be introduced later when the upload path needs to scale.

## Integration events

Potential events:

- `UserProfileCreated`;
- `UserProfileUpdated`;
- `UserPhotoUpdated`;
- `UserNotificationPreferencesChanged`.

Consumers should be idempotent. Events should contain the profile ID and changed
business data, but never credentials or tokens.

Profile creation can initially be lazy: the first authenticated `GET /me` creates
a minimal profile if one does not exist. This avoids coupling the service to
Keycloak's internal database or administrative API.

## Implementation order

1. Create the service with its own PostgreSQL schema and migrations.
2. Add JWT validation and `GET/PUT /api/v1/me`.
3. Add profile tests and API contract documentation.
4. Add MinIO and photo upload/delete.
5. Add integration events.
6. Add profile read models or API composition only where a product feature needs them.

The service should be introduced after the Keycloak PR is verified and before
features that require user preferences or richer volunteer profiles.
