# Architecture Decisions

This document records decisions that should guide future changes. It is
intentionally concise; detailed implementation notes belong in the relevant
service documentation.

## ADR-001: Keycloak is the identity provider

**Status:** Accepted

Keycloak is responsible for authentication and identity infrastructure. It
stores credentials and manages login sessions, password reset, identity-provider
integration, and coarse-grained realm roles.

Business services validate JWTs and use the opaque `sub` claim as the user
reference. They do not implement password authentication themselves.

## ADR-002: User Profile is a separate service

**Status:** Accepted

Application-level profile data is separated from authentication. The planned
User Profile Service owns display data, preferences, consents, and avatar
metadata. It has its own database and is addressed by the Keycloak subject ID.

This keeps identity concerns isolated and provides a useful future microservice
boundary without duplicating credential management.

## ADR-003: Keycloak will have a dedicated PostgreSQL database

**Status:** Accepted

Keycloak uses its own PostgreSQL database, separate from business-service
databases. The local Compose setup persists it in the `keycloak_pgdata` volume
and imports the demo realm and users on first startup.

Application services must never query the Keycloak database directly. Database
credentials, backups, persistent storage, and deployment secrets are
infrastructure concerns. The committed defaults are for local development only.

## ADR-004: RabbitMQ and Kafka have different responsibilities

**Status:** Accepted

RabbitMQ is used for business commands, workflow messages, and asynchronous
service communication. Kafka is used for durable activity/event streaming and
analytics.

The same message should not be duplicated across both systems unless there is a
clear, documented reason.

## ADR-005: Profile lookups are not required for core writes

**Status:** Accepted

Core operations such as event registration must remain available when the
Profile Service or avatar storage is temporarily unavailable. If another
service needs profile display data, use API composition, a local read model, or
a deliberate snapshot rather than a synchronous dependency in the write path.
