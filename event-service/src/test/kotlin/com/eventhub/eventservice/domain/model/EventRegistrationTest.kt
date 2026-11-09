package com.eventhub.eventservice.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventRegistrationTest {

    @Test
    fun `should create valid event registration`() {
        val eventId = UUID.randomUUID()
        val registration = EventRegistration(
            eventId = eventId,
            userId = "user-123"
        )

        assertEquals(eventId, registration.eventId)
        assertEquals("user-123", registration.userId)
        assertEquals(RegistrationStatus.REGISTERED, registration.status)
        assertNotNull(registration.registeredAt)
    }

    @Test
    fun `should throw exception when user id is blank`() {
        assertThrows<IllegalArgumentException> {
            EventRegistration(
                eventId = UUID.randomUUID(),
                userId = "   "
            )
        }
    }

    @Test
    fun `should cancel registration`() {
        val registration = EventRegistration(
            eventId = UUID.randomUUID(),
            userId = "user-123"
        )

        val cancelled = registration.cancel()
        assertEquals(RegistrationStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `should re-register cancelled registration`() {
        val registration = EventRegistration(
            eventId = UUID.randomUUID(),
            userId = "user-123",
            status = RegistrationStatus.CANCELLED
        )

        val reRegistered = registration.reRegister()
        assertEquals(RegistrationStatus.REGISTERED, reRegistered.status)
    }

    @Test
    fun `should fail to re-register active registration`() {
        val registration = EventRegistration(
            eventId = UUID.randomUUID(),
            userId = "user-123",
            status = RegistrationStatus.REGISTERED
        )

        assertThrows<IllegalStateException> {
            registration.reRegister()
        }
    }
}



