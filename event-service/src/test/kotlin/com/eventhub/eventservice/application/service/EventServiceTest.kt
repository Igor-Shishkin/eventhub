package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.UUID

class EventServiceTest {

    private val eventRepository: EventRepositoryPort = mockk()
    private val eventService = EventService(eventRepository)
    private val now = Instant.parse("2026-01-01T10:00:00Z")

    @BeforeEach
    fun setUp() {
        clearMocks(eventRepository)
    }

    @Test
    fun `should create event successfully`() = runTest {
        val command = CreateEventCommand(
            title = "Kotlin Conf",
            description = "Annual Kotlin Conference",
            location = "Online",
            organizerId = "org-kotlin",
            startTime = now
        )
        val savedEvent = Event(
            id = UUID.randomUUID(),
            title = command.title,
            description = command.description,
            location = command.location,
            organizerId = command.organizerId,
            startTime = command.startTime
        )
        coEvery { eventRepository.save(any()) } returns savedEvent

        val created = eventService.createEvent(command)

        assertNotNull(created.id)
        assertEquals("Kotlin Conf", created.title)
        assertEquals("org-kotlin", created.organizerId)
        assertEquals(now, created.startTime)
        assertEquals(EventStatus.DRAFT, created.status)
        coVerify(exactly = 1) { eventRepository.save(match { it.title == command.title && it.organizerId == command.organizerId }) }
    }

    @Test
    fun `should get event by id`() = runTest {
        val eventId = UUID.randomUUID()
        val event = Event(id = eventId, title = "Workshop", organizerId = "org-123", startTime = now)
        coEvery { eventRepository.findById(eventId) } returns event

        val found = eventService.getEventById(eventId)

        assertEquals(eventId, found.id)
        assertEquals("Workshop", found.title)
        assertEquals("org-123", found.organizerId)
        assertEquals(now, found.startTime)
        coVerify(exactly = 1) { eventRepository.findById(eventId) }
    }

    @Test
    fun `should throw EventNotFoundException when event not found by id`() = runTest {
        val nonExistentId = UUID.randomUUID()
        coEvery { eventRepository.findById(nonExistentId) } returns null

        assertThrows<EventNotFoundException> {
            eventService.getEventById(nonExistentId)
        }
        coVerify(exactly = 1) { eventRepository.findById(nonExistentId) }
    }

    @Test
    @Suppress("UnusedFlow", "CheckResult", "UNUSED_EXPRESSION")
    fun `should return all events`() = runTest {
        val eventsList = listOf(
            Event(id = UUID.randomUUID(), title = "Event 1", organizerId = "org-1", startTime = now),
            Event(id = UUID.randomUUID(), title = "Event 2", organizerId = "org-2", startTime = now)
        )
        every { eventRepository.findAll() } returns eventsList.asFlow()

        val events = eventService.getAllEvents().toList()

        assertEquals(2, events.size)
        assertEquals("Event 1", events[0].title)
        assertEquals("Event 2", events[1].title)
        verify(exactly = 1) { eventRepository.findAll() }
    }

    @Test
    fun `should publish event`() = runTest {
        val eventId = UUID.randomUUID()
        val draftEvent = Event(id = eventId, title = "Draft Event", organizerId = "org-123", startTime = now, status = EventStatus.DRAFT)
        val publishedEvent = draftEvent.publish()

        coEvery { eventRepository.findById(eventId) } returns draftEvent
        coEvery { eventRepository.save(any()) } returns publishedEvent

        val published = eventService.publishEvent(eventId, "org-123")

        assertEquals(EventStatus.PUBLISHED, published.status)
        coVerify(exactly = 1) { eventRepository.findById(eventId) }
        coVerify(exactly = 1) { eventRepository.save(match { it.status == EventStatus.PUBLISHED }) }
    }

    @Test
    fun `should cancel event`() = runTest {
        val eventId = UUID.randomUUID()
        val activeEvent = Event(id = eventId, title = "Active Event", organizerId = "org-123", startTime = now, status = EventStatus.PUBLISHED)
        val cancelledEvent = activeEvent.cancel()

        coEvery { eventRepository.findById(eventId) } returns activeEvent
        coEvery { eventRepository.save(any()) } returns cancelledEvent

        val cancelled = eventService.cancelEvent(eventId, "org-123")

        assertEquals(EventStatus.CANCELLED, cancelled.status)
        coVerify(exactly = 1) { eventRepository.findById(eventId) }
        coVerify(exactly = 1) { eventRepository.save(match { it.status == EventStatus.CANCELLED }) }
    }
}
