package com.eventhub.eventservice.infrastructure.rest

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.application.port.`in`.EventUseCase
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.infrastructure.rest.dto.CreateEventRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

class EventControllerTest {

    private lateinit var webTestClient: WebTestClient
    private lateinit var fakeUseCase: FakeEventUseCase

    @BeforeEach
    fun setUp() {
        fakeUseCase = FakeEventUseCase()
        val controller = EventController(fakeUseCase)
        webTestClient = WebTestClient.bindToController(controller)
            .controllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `should create event and return 201 Created`() {
        val request = CreateEventRequest(
            title = "Spring Boot Workshop",
            description = "Hands-on workshop",
            location = "Krakow"
        )

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("Spring Boot Workshop")
            .jsonPath("$.status").isEqualTo("DRAFT")
    }

    @Test
    fun `should return 400 Bad Request when creating event with blank title`() {
        val request = mapOf("title" to "   ")

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation Error")
            .jsonPath("$.errors.title").isEqualTo("Title cannot be blank")
    }

    @Test
    fun `should get event by id`() {
        val eventId = UUID.randomUUID()
        fakeUseCase.events[eventId] = Event(
            id = eventId,
            title = "Existing Event",
            status = EventStatus.PUBLISHED
        )

        webTestClient.get()
            .uri("/api/v1/events/$eventId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(eventId.toString())
            .jsonPath("$.title").isEqualTo("Existing Event")
            .jsonPath("$.status").isEqualTo("PUBLISHED")
    }

    @Test
    fun `should return 404 Not Found when event does not exist`() {
        val nonExistentId = UUID.randomUUID()

        webTestClient.get()
            .uri("/api/v1/events/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.title").isEqualTo("Not Found")
    }

    @Test
    fun `should get all events`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        fakeUseCase.events[id1] = Event(id = id1, title = "Event 1")
        fakeUseCase.events[id2] = Event(id = id2, title = "Event 2")

        webTestClient.get()
            .uri("/api/v1/events")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
    }

    @Test
    fun `should publish event`() {
        val eventId = UUID.randomUUID()
        fakeUseCase.events[eventId] = Event(id = eventId, title = "Draft Event", status = EventStatus.DRAFT)

        webTestClient.post()
            .uri("/api/v1/events/$eventId/publish")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("PUBLISHED")
    }

    @Test
    fun `should return 409 Conflict when publishing already published event`() {
        val eventId = UUID.randomUUID()
        fakeUseCase.events[eventId] = Event(id = eventId, title = "Published Event", status = EventStatus.PUBLISHED)

        webTestClient.post()
            .uri("/api/v1/events/$eventId/publish")
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Conflict")
    }

    @Test
    fun `should cancel event`() {
        val eventId = UUID.randomUUID()
        fakeUseCase.events[eventId] = Event(id = eventId, title = "Event", status = EventStatus.PUBLISHED)

        webTestClient.post()
            .uri("/api/v1/events/$eventId/cancel")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("CANCELLED")
    }

    private class FakeEventUseCase : EventUseCase {
        val events = mutableMapOf<UUID, Event>()

        override suspend fun createEvent(command: CreateEventCommand): Event {
            val event = Event(
                id = UUID.randomUUID(),
                title = command.title,
                description = command.description,
                location = command.location,
                startTime = command.startTime,
                endTime = command.endTime
            )
            events[event.id!!] = event
            return event
        }

        override suspend fun getEventById(id: UUID): Event {
            return events[id] ?: throw EventNotFoundException(id)
        }

        override fun getAllEvents(): Flow<Event> = events.values.asFlow()

        override suspend fun publishEvent(id: UUID): Event {
            val event = getEventById(id)
            val published = event.publish()
            events[id] = published
            return published
        }

        override suspend fun cancelEvent(id: UUID): Event {
            val event = getEventById(id)
            val cancelled = event.cancel()
            events[id] = cancelled
            return cancelled
        }
    }
}

