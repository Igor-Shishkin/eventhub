# EventHub – Copilot Instructions

## Project context
Before making significant changes, read:
- [Project Context](../docs/PROJECT_CONTEXT.md)
- [README](../README.md)

## General rules
- Follow the existing architecture and technology choices.
- Architecture pattern: Hexagonal Architecture (Ports and Adapters):
  - `domain`: entities, domain models, business validation, outbound/inbound ports (interfaces).
  - `application`: use cases, commands, application services.
  - `infrastructure`: inbound adapters (WebFlux REST controllers, DTOs, exception handlers) and outbound adapters (R2DBC repositories, messaging publishers).
- Reactive programming: Kotlin Coroutines (`suspend`, `Flow`), Spring WebFlux, Spring Data R2DBC.
- Testing: JUnit 5, Kotlin Coroutines Test, and MockK for mocking/stubbing (avoid ad-hoc in-memory fake classes).
- Do not introduce new frameworks or dependencies without discussing them first.
- Keep microservices independent and respect their data ownership.
- Do not access another service's database directly.
- Prefer simple solutions over unnecessary abstractions.
- Add tests for new business logic.
- Update documentation when architectural decisions change.

## Development approach
- Work incrementally.
- Before implementing a significant feature, briefly explain the proposed approach.
- Do not assume planned functionality is already implemented.
- If requirements are unclear, ask before making architectural decisions.