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
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.web.reactive.server.WebTestClient
import kotlinx.coroutines.test.runTest
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
    fun `should create event from the authenticated organizer`() = runTest {
        val request = CreateEventRequest(
            title = "Spring Boot Workshop",
            description = "Hands-on workshop",
            location = "Krakow",
            startTime = now
        )
        val createdEvent = Event(
            id = UUID.randomUUID(),
            title = request.title,
            description = request.description,
            location = request.location,
            organizerId = "org-123",
            startTime = request.startTime!!
        )
        coEvery { eventUseCase.createEvent(any()) } returns createdEvent

        val response = EventController(eventUseCase).createEvent(request, jwt("org-123"))

        assert(response.organizerId == "org-123")
        coVerify(exactly = 1) { eventUseCase.createEvent(request.toCommand("org-123")) }
    }

    @Test
    fun `should return 400 Bad Request when creating event with blank title`() {
        val request = mapOf(
            "title" to "   ",
            "startTime" to "2026-01-01T10:00:00Z"
        )

        webTestClient.mutateWith(mockJwt().jwt { it.subject("org-123") }).post()
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
    fun `should return 400 Bad Request when creating event without startTime`() {
        val request = mapOf(
            "title" to "Spring Boot Workshop",
        )

        webTestClient.mutateWith(mockJwt().jwt { it.subject("org-123") }).post()
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
    fun `should publish event`() = runTest {
        val eventId = UUID.randomUUID()
        val publishedEvent = Event(id = eventId, title = "Draft Event", organizerId = "org-123", startTime = now, status = EventStatus.PUBLISHED)
        coEvery { eventUseCase.publishEvent(eventId, "org-123") } returns publishedEvent

        val response = EventController(eventUseCase).publishEvent(eventId, jwt("org-123"))

        assert(response.status == EventStatus.PUBLISHED)
        coVerify(exactly = 1) { eventUseCase.publishEvent(eventId, "org-123") }
    }

    @Test
    fun `should return conflict when publishing already published event`() = runTest {
        val eventId = UUID.randomUUID()
        coEvery { eventUseCase.publishEvent(eventId, "org-123") } throws IllegalStateException("Only draft events can be published")

        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            EventController(eventUseCase).publishEvent(eventId, jwt("org-123"))
        }

        assert(exception.message == "Only draft events can be published")
    }

    @Test
    fun `should cancel event`() = runTest {
        val eventId = UUID.randomUUID()
        val cancelledEvent = Event(id = eventId, title = "Event", organizerId = "org-123", startTime = now, status = EventStatus.CANCELLED)
        coEvery { eventUseCase.cancelEvent(eventId, "org-123") } returns cancelledEvent

        val response = EventController(eventUseCase).cancelEvent(eventId, jwt("org-123"))

        assert(response.status == EventStatus.CANCELLED)
        coVerify(exactly = 1) { eventUseCase.cancelEvent(eventId, "org-123") }
    }

    private fun jwt(subject: String): Jwt = Jwt.withTokenValue("test-token")
        .header("alg", "none")
        .subject(subject)
        .build()
}
