package com.eventhub.eventservice.infrastructure.persistence.entity

import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.model.RegistrationStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("event_registrations")
data class EventRegistrationEntity(
    @Id
    val id: UUID? = null,
    @Column("event_id")
    val eventId: UUID,
    @Column("user_id")
    val userId: String,
    val status: String,
    @Column("registered_at")
    val registeredAt: Instant
) {
    fun toDomain(): EventRegistration = EventRegistration(
        id = id,
        eventId = eventId,
        userId = userId,
        status = RegistrationStatus.valueOf(status),
        registeredAt = registeredAt
    )
}

