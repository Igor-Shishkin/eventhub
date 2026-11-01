# EventHub – Copilot Instructions

## Project context
Before making significant changes, read:
- [Project Context](../docs/PROJECT_CONTEXT.md)
- [README](../README.md)

## General rules
- Follow the existing architecture and technology choices.
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