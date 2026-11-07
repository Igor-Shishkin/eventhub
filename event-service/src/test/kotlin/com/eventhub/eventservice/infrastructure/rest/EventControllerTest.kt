package com.eventhub.eventservice.infrastructure.rest

import com.eventhub.eventservice.application.port.`in`.EventUseCase
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.infrastructure.rest.dto.CreateEventRequest
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.asFlow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.UUID

class EventControllerTest {

    private val eventUseCase: EventUseCase = mockk()
    private lateinit var webTestClient: WebTestClient
    private val now = Instant.parse("2026-01-01T10:00:00Z")

    @BeforeEach
    fun setUp() {
        clearMocks(eventUseCase)
        val controller = EventController(eventUseCase)
        webTestClient = WebTestClient.bindToController(controller)
            .controllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `should create event and return 201 Created`() {
        val request = CreateEventRequest(
            title = "Spring Boot Workshop",
            description = "Hands-on workshop",
            location = "Krakow",
            organizerId = "org-123",
            startTime = now
        )
        val createdEvent = Event(
            id = UUID.randomUUID(),
            title = request.title,
            description = request.description,
            location = request.location,
            organizerId = request.organizerId,
            startTime = request.startTime!!
        )
        coEvery { eventUseCase.createEvent(any()) } returns createdEvent

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("Spring Boot Workshop")
            .jsonPath("$.organizerId").isEqualTo("org-123")
            .jsonPath("$.startTime").isEqualTo("2026-01-01T10:00:00Z")
            .jsonPath("$.status").isEqualTo("DRAFT")

        coVerify(exactly = 1) { eventUseCase.createEvent(request.toCommand()) }
    }

    @Test
    fun `should return 400 Bad Request when creating event with blank title`() {
        val request = mapOf(
            "title" to "   ",
            "organizerId" to "org-123",
            "startTime" to "2026-01-01T10:00:00Z"
        )

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation Error")
            .jsonPath("$.errors.title").isEqualTo("Title cannot be blank")

        coVerify(exactly = 0) { eventUseCase.createEvent(any()) }
    }

    @Test
    fun `should return 400 Bad Request when creating event with blank organizerId`() {
        val request = mapOf(
            "title" to "Spring Boot Workshop",
            "organizerId" to "   ",
            "startTime" to "2026-01-01T10:00:00Z"
        )

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation Error")
            .jsonPath("$.errors.organizerId").isEqualTo("Organizer ID cannot be blank")

        coVerify(exactly = 0) { eventUseCase.createEvent(any()) }
    }

    @Test
    fun `should return 400 Bad Request when creating event without startTime`() {
        val request = mapOf(
            "title" to "Spring Boot Workshop",
            "organizerId" to "org-123"
        )

        webTestClient.post()
            .uri("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation Error")
            .jsonPath("$.errors.startTime").isEqualTo("Start time cannot be null")

        coVerify(exactly = 0) { eventUseCase.createEvent(any()) }
    }

    @Test
    fun `should get event by id`() {
        val eventId = UUID.randomUUID()
        val event = Event(
            id = eventId,
            title = "Existing Event",
            organizerId = "org-123",
            startTime = now,
            status = EventStatus.PUBLISHED
        )
        coEvery { eventUseCase.getEventById(eventId) } returns event

        webTestClient.get()
            .uri("/api/v1/events/$eventId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(eventId.toString())
            .jsonPath("$.title").isEqualTo("Existing Event")
            .jsonPath("$.organizerId").isEqualTo("org-123")
            .jsonPath("$.startTime").isEqualTo("2026-01-01T10:00:00Z")
            .jsonPath("$.status").isEqualTo("PUBLISHED")

        coVerify(exactly = 1) { eventUseCase.getEventById(eventId) }
    }

    @Test
    fun `should return 404 Not Found when event does not exist`() {
        val nonExistentEventId = UUID.randomUUID()
        coEvery { eventUseCase.getEventById(nonExistentEventId) } throws EventNotFoundException(nonExistentEventId)

        webTestClient.get()
            .uri("/api/v1/events/$nonExistentEventId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.title").isEqualTo("Not Found")

        coVerify(exactly = 1) { eventUseCase.getEventById(nonExistentEventId) }
    }

    @Test
    @Suppress("UnusedFlow", "CheckResult", "UNUSED_EXPRESSION")
    fun `should get all events`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val events = listOf(
            Event(id = id1, title = "Event 1", organizerId = "org-1", startTime = now),
            Event(id = id2, title = "Event 2", organizerId = "org-2", startTime = now)
        )
        every { eventUseCase.getAllEvents() } returns events.asFlow()

        webTestClient.get()
            .uri("/api/v1/events")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)

        verify(exactly = 1) { eventUseCase.getAllEvents() }
    }

    @Test
    fun `should publish event`() {
        val eventId = UUID.randomUUID()
        val publishedEvent = Event(id = eventId, title = "Draft Event", organizerId = "org-123", startTime = now, status = EventStatus.PUBLISHED)
        coEvery { eventUseCase.publishEvent(eventId) } returns publishedEvent

        webTestClient.post()
            .uri("/api/v1/events/$eventId/publish")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("PUBLISHED")

        coVerify(exactly = 1) { eventUseCase.publishEvent(eventId) }
    }

    @Test
    fun `should return 409 Conflict when publishing already published event`() {
        val eventId = UUID.randomUUID()
        coEvery { eventUseCase.publishEvent(eventId) } throws IllegalStateException("Only draft events can be published")

        webTestClient.post()
            .uri("/api/v1/events/$eventId/publish")
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Conflict")

        coVerify(exactly = 1) { eventUseCase.publishEvent(eventId) }
    }

    @Test
    fun `should cancel event`() {
        val eventId = UUID.randomUUID()
        val cancelledEvent = Event(id = eventId, title = "Event", organizerId = "org-123", startTime = now, status = EventStatus.CANCELLED)
        coEvery { eventUseCase.cancelEvent(eventId) } returns cancelledEvent

        webTestClient.post()
            .uri("/api/v1/events/$eventId/cancel")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("CANCELLED")

        coVerify(exactly = 1) { eventUseCase.cancelEvent(eventId) }
    }
}

