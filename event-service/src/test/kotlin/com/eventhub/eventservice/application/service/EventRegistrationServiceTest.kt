package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CancelRegistrationCommand
import com.eventhub.eventservice.application.port.`in`.RegisterAttendeeCommand
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.exception.RegistrationNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.domain.model.RegistrationStatus
import com.eventhub.eventservice.domain.port.EventRegistrationRepositoryPort
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

class EventRegistrationServiceTest {

    private val eventRepository: EventRepositoryPort = mockk()
    private val registrationRepository: EventRegistrationRepositoryPort = mockk()
    private val registrationService = EventRegistrationService(eventRepository, registrationRepository)

    private val now = Instant.parse("2026-05-01T12:00:00Z")
    private val eventId = UUID.randomUUID()
    private val userId = "user-test-1"

    private val publishedEvent = Event(
        id = eventId,
        title = "Cloud Native Meetup",
        organizerId = "org-1",
        status = EventStatus.PUBLISHED,
        startTime = now
    )

    @BeforeEach
    fun setUp() {
        clearMocks(eventRepository, registrationRepository)
    }

    @Test
    fun `should register attendee successfully when event is published and not registered yet`() = runTest {
        coEvery { eventRepository.findById(eventId) } returns publishedEvent
        coEvery { registrationRepository.findByEventIdAndUserId(eventId, userId) } returns null
        coEvery { registrationRepository.save(any()) } answers {
            firstArg<EventRegistration>().copy(id = UUID.randomUUID())
        }

        val result = registrationService.registerAttendee(RegisterAttendeeCommand(eventId, userId))

        assertNotNull(result.id)
        assertEquals(eventId, result.eventId)
        assertEquals(userId, result.userId)
        assertEquals(RegistrationStatus.REGISTERED, result.status)
        coVerify(exactly = 1) { registrationRepository.save(match { it.eventId == eventId && it.userId == userId }) }
    }

    @Test
    fun `should throw EventNotFoundException when event does not exist`() = runTest {
        coEvery { eventRepository.findById(eventId) } returns null

        assertThrows<EventNotFoundException> {
            registrationService.registerAttendee(RegisterAttendeeCommand(eventId, userId))
        }
        coVerify(exactly = 0) { registrationRepository.save(any()) }
    }

    @Test
    fun `should throw IllegalStateException when registering for draft event`() = runTest {
        val draftEvent = publishedEvent.copy(status = EventStatus.DRAFT)
        coEvery { eventRepository.findById(eventId) } returns draftEvent

        val ex = assertThrows<IllegalStateException> {
            registrationService.registerAttendee(RegisterAttendeeCommand(eventId, userId))
        }
        assertEquals("Cannot register for an event that is not PUBLISHED (current status: DRAFT)", ex.message)
        coVerify(exactly = 0) { registrationRepository.save(any()) }
    }

    @Test
    fun `should throw IllegalStateException when user is already registered`() = runTest {
        val existing = EventRegistration(
            id = UUID.randomUUID(),
            eventId = eventId,
            userId = userId,
            status = RegistrationStatus.REGISTERED
        )
        coEvery { eventRepository.findById(eventId) } returns publishedEvent
        coEvery { registrationRepository.findByEventIdAndUserId(eventId, userId) } returns existing

        val ex = assertThrows<IllegalStateException> {
            registrationService.registerAttendee(RegisterAttendeeCommand(eventId, userId))
        }
        assertEquals("User '$userId' is already registered for event '$eventId'", ex.message)
        coVerify(exactly = 0) { registrationRepository.save(any()) }
    }

    @Test
    fun `should re-register when user has previously cancelled registration`() = runTest {
        val existingCancelled = EventRegistration(
            id = UUID.randomUUID(),
            eventId = eventId,
            userId = userId,
            status = RegistrationStatus.CANCELLED
        )
        coEvery { eventRepository.findById(eventId) } returns publishedEvent
        coEvery { registrationRepository.findByEventIdAndUserId(eventId, userId) } returns existingCancelled
        coEvery { registrationRepository.save(any()) } answers { firstArg() }

        val result = registrationService.registerAttendee(RegisterAttendeeCommand(eventId, userId))

        assertEquals(RegistrationStatus.REGISTERED, result.status)
        assertEquals(existingCancelled.id, result.id)
        coVerify(exactly = 1) { registrationRepository.save(match { it.status == RegistrationStatus.REGISTERED }) }
    }

    @Test
    fun `should cancel registration successfully`() = runTest {
        val existing = EventRegistration(
            id = UUID.randomUUID(),
            eventId = eventId,
            userId = userId,
            status = RegistrationStatus.REGISTERED
        )
        coEvery { registrationRepository.findByEventIdAndUserId(eventId, userId) } returns existing
        coEvery { registrationRepository.save(any()) } answers { firstArg() }

        val result = registrationService.cancelRegistration(CancelRegistrationCommand(eventId, userId))

        assertEquals(RegistrationStatus.CANCELLED, result.status)
        coVerify(exactly = 1) { registrationRepository.save(match { it.status == RegistrationStatus.CANCELLED }) }
    }

    @Test
    fun `should throw RegistrationNotFoundException when cancelling non-existing registration`() = runTest {
        coEvery { registrationRepository.findByEventIdAndUserId(eventId, userId) } returns null

        assertThrows<RegistrationNotFoundException> {
            registrationService.cancelRegistration(CancelRegistrationCommand(eventId, userId))
        }
        coVerify(exactly = 0) { registrationRepository.save(any()) }
    }

    @Test
    fun `should return flow of registrations for event`() = runTest {
        val reg1 = EventRegistration(eventId = eventId, userId = "u1")
        val reg2 = EventRegistration(eventId = eventId, userId = "u2")
        coEvery { registrationRepository.findByEventId(eventId) } returns listOf(reg1, reg2).asFlow()

        val results = registrationService.getRegistrationsForEvent(eventId).toList()

        assertEquals(2, results.size)
        assertEquals("u1", results[0].userId)
        assertEquals("u2", results[1].userId)
    }
}

