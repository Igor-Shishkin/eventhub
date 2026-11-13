# EventHub – Copilot Instructions

## Project context
Before making significant changes, read:
- [Project Context](../docs/PROJECT_CONTEXT.md)
- [README](../README.md)

## General rules
- Follow the existing architecture and technology choices.
- Architecture pattern: Hexagonal Architecture (Ports and Adapters):
  - `domain`: entities, domain models, business validation, outbound/inbound ports (interfaces).
  - `application`: use cases, commands, application services. Keep one command/use case per file.
  - `infrastructure`: inbound adapters (WebFlux REST controllers, DTOs, exception handlers) and outbound adapters (R2DBC repositories, messaging publishers).
- Reactive programming: Kotlin Coroutines (`suspend`, `Flow`), Spring WebFlux, Spring Data R2DBC.
- Testing: JUnit 5, Kotlin Coroutines Test, and MockK for mocking/stubbing (avoid ad-hoc in-memory fake classes).
- Frontend (Angular):
  - Use PrimeNG and PrimeIcons components instead of writing custom CSS styles wherever possible.
  - Decompose views into small, reusable standalone components following the Single Responsibility Principle.
  - Development API communication relies on the Angular dev-server proxy (`proxy.conf.json`) to forward `/api` requests to backend services without CORS configuration on backend microservices.
- Do not introduce new frameworks or dependencies without discussing them first.
- Keep microservices independent and respect their data ownership.
- Do not access another service's database directly.
- Keycloak uses the separate `postgres-keycloak` database in local Compose;
  services must use JWT validation rather than querying it.
- Keep realm seed data limited to authentication identity and realm roles;
  application profile fields belong to the future User Profile Service.
- Prefer simple solutions over unnecessary abstractions.
- Add tests for new business logic.
- Update documentation when architectural decisions change.
- The realm file is a first-start seed. After changing demo users or roles,
  reset local volumes with `docker compose down -v` before validating again.
- After each meaningful code change or development step, update `docs/PROJECT_CONTEXT.md` with the implemented behavior, validation status, and next steps; update `README.md` or other relevant documentation when setup or user-facing behavior changes.

## Development approach
- Work incrementally.
- Before implementing a significant feature, briefly explain the proposed approach.
- Do not assume planned functionality is already implemented.
- If requirements are unclear, ask before making architectural decisions.
