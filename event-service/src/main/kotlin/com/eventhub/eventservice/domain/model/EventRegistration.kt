package com.eventhub.eventservice.domain.model

import com.eventhub.eventservice.infrastructure.persistence.entity.EventRegistrationEntity
import java.time.Instant
import java.util.UUID

data class EventRegistration(
    val id: UUID? = null,
    val eventId: UUID,
    val userId: String,
    val status: RegistrationStatus = RegistrationStatus.REGISTERED,
    val registeredAt: Instant = Instant.now()
) {
    init {
        require(userId.isNotBlank()) { "User ID cannot be blank" }
    }

    fun cancel(): EventRegistration {
        check(status != RegistrationStatus.CANCELLED) { "Registration is already cancelled" }
        return copy(status = RegistrationStatus.CANCELLED)
    }

    fun reRegister(): EventRegistration {
        check(status == RegistrationStatus.CANCELLED) { "Registration is already active" }
        return copy(status = RegistrationStatus.REGISTERED, registeredAt = Instant.now())
    }

    fun toEntity(): EventRegistrationEntity = EventRegistrationEntity(
        id = id,
        eventId = eventId,
        userId = userId,
        status = status.name,
        registeredAt = registeredAt
    )
}

