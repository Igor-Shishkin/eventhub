# RabbitMQ business messaging

## Scope of this change

The first RabbitMQ vertical slice connects the Event Service with a minimal
Notification Service. It publishes an `EventRegistrationCreated` message after
a successful event registration. The Notification Service consumes the message
and currently logs a registration confirmation instead of sending a real email.

This is intentionally a direct publisher implementation. The database write and
message publication are not atomic yet. A Transactional Outbox is a follow-up
step and must be introduced before treating this workflow as production-ready.

## Responsibilities

- **Event Service** owns event registrations and publishes business events.
- **RabbitMQ** routes messages and stores them in durable queues while consumers
  are unavailable.
- **Notification Service** consumes registration events and owns notification
  delivery. It must not be replaced by RabbitMQ: RabbitMQ is the broker, not the
  email or push notification provider.

## Topology

```text
Exchange: eventhub.business (topic)
Routing key: event.registration-created.v1
Queue: notification-service.event-registration-created

Event Service ── EventRegistrationCreated ──▶ RabbitMQ exchange
                                                   │
                                                   ▼
                                      Notification Service queue
```

The notification queue is separate from any future consumer queue. If another
service needs the same event, it gets its own queue and binding.

## Message contract

```json
{
  "eventId": "uuid",
  "registrationId": "uuid",
  "userId": "opaque-user-id",
  "eventTitle": "Cloud Native Meetup",
  "eventStartTime": "2026-06-01T18:00:00Z",
  "registeredAt": "2026-05-01T12:00:00Z"
}
```

The contract contains the data needed for a local registration confirmation.
It does not contain passwords, access tokens, refresh tokens, or a complete JWT.

## Local development

Start RabbitMQ and the Event Service database:

```bash
docker compose up -d postgres-event rabbitmq
```

RabbitMQ endpoints:

- AMQP: `localhost:5672`
- Management UI: `http://localhost:15672`
- Development credentials: `eventhub` / `eventhub_password`

Run the services locally:

```bash
./event-service/gradlew -p event-service bootRun
./notification-service/gradlew -p notification-service bootRun
```

Or run both applications in Docker:

```bash
docker compose --profile apps up -d --build
```

When an attendee registers for a published event, the Notification Service logs
the prepared confirmation. A real email provider will be added in a later slice.

## Configuration portability

The applications use environment variables for broker connection details:

- local Gradle execution defaults to `localhost`;
- Docker Compose sets `RABBITMQ_HOST=rabbitmq`;
- Kubernetes can use the RabbitMQ Service DNS name, for example
  `RABBITMQ_HOST=rabbitmq`.

No RabbitMQ-specific Docker or Kubernetes API is used by the application code.
