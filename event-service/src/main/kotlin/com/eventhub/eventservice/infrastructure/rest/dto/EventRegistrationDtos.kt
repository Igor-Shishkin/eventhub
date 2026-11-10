package com.eventhub.eventservice.infrastructure.rest.dto

import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.model.RegistrationStatus
import java.time.Instant
import java.util.UUID

data class EventRegistrationResponse(
    val id: UUID,
    val eventId: UUID,
    val userId: String,
    val status: RegistrationStatus,
    val registeredAt: Instant
) {
    companion object {
        fun from(registration: EventRegistration): EventRegistrationResponse = EventRegistrationResponse(
            id = requireNotNull(registration.id) { "Registration ID must not be null" },
            eventId = registration.eventId,
            userId = registration.userId,
            status = registration.status,
            registeredAt = registration.registeredAt
        )
    }
}
