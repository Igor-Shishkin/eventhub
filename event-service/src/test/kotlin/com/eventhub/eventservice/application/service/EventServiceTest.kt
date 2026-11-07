package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class EventServiceTest {

    private lateinit var fakeRepository: InMemoryEventRepository
    private lateinit var eventService: EventService

    @BeforeEach
    fun setUp() {
        fakeRepository = InMemoryEventRepository()
        eventService = EventService(fakeRepository)
    }

    @Test
    fun `should create event successfully`() = runTest {
        val command = CreateEventCommand(
            title = "Kotlin Conf",
            description = "Annual Kotlin Conference",
            location = "Online"
        )

        val created = eventService.createEvent(command)

        assertNotNull(created.id)
        assertEquals("Kotlin Conf", created.title)
        assertEquals(EventStatus.DRAFT, created.status)
        assertEquals(created, fakeRepository.findById(created.id!!))
    }

    @Test
    fun `should get event by id`() = runTest {
        val event = Event(id = UUID.randomUUID(), title = "Workshop")
        fakeRepository.save(event)

        val found = eventService.getEventById(event.id!!)

        assertEquals(event.id, found.id)
        assertEquals("Workshop", found.title)
    }

    @Test
    fun `should throw EventNotFoundException when event not found by id`() = runTest {
        val nonExistentId = UUID.randomUUID()

        assertThrows<EventNotFoundException> {
            eventService.getEventById(nonExistentId)
        }
    }

    @Test
    fun `should return all events`() = runTest {
        fakeRepository.save(Event(id = UUID.randomUUID(), title = "Event 1"))
        fakeRepository.save(Event(id = UUID.randomUUID(), title = "Event 2"))

        val events = eventService.getAllEvents().toList()

        assertEquals(2, events.size)
    }

    @Test
    fun `should publish event`() = runTest {
        val eventId = UUID.randomUUID()
        val event = Event(id = eventId, title = "Draft Event", status = EventStatus.DRAFT)
        fakeRepository.save(event)

        val published = eventService.publishEvent(eventId)

        assertEquals(EventStatus.PUBLISHED, published.status)
        val savedInRepo = fakeRepository.findById(eventId)
        assertEquals(EventStatus.PUBLISHED, savedInRepo?.status)
    }

    @Test
    fun `should cancel event`() = runTest {
        val eventId = UUID.randomUUID()
        val event = Event(id = eventId, title = "Active Event", status = EventStatus.PUBLISHED)
        fakeRepository.save(event)

        val cancelled = eventService.cancelEvent(eventId)

        assertEquals(EventStatus.CANCELLED, cancelled.status)
        val savedInRepo = fakeRepository.findById(eventId)
        assertEquals(EventStatus.CANCELLED, savedInRepo?.status)
    }

    private class InMemoryEventRepository : EventRepositoryPort {
        private val storage = ConcurrentHashMap<UUID, Event>()

        override suspend fun save(event: Event): Event {
            val id = event.id ?: UUID.randomUUID()
            val eventToSave = event.copy(id = id)
            storage[id] = eventToSave
            return eventToSave
        }

        override suspend fun findById(id: UUID): Event? = storage[id]

        override fun findAll(): Flow<Event> = storage.values.asFlow()

        override suspend fun deleteById(id: UUID) {
            storage.remove(id)
        }
    }
}

