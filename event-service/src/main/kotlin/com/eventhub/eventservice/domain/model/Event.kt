package com.eventhub.eventservice.domain.model

import com.eventhub.eventservice.infrastructure.persistence.entity.EventEntity
import java.time.Instant
import java.util.UUID

data class Event(
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val status: EventStatus = EventStatus.DRAFT,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        if (startTime != null && endTime != null) {
            require(endTime.isAfter(startTime)) { "End time must be after start time" }
        }
    }

    fun publish(): Event {
        check(status == EventStatus.DRAFT) { "Only draft events can be published" }
        return copy(status = EventStatus.PUBLISHED, updatedAt = Instant.now())
    }

    fun cancel(): Event {
        check(status != EventStatus.CANCELLED) { "Event is already cancelled" }
        return copy(status = EventStatus.CANCELLED, updatedAt = Instant.now())
    }

    fun toEntity(): EventEntity = EventEntity(
        id = id,
        title = title,
        description = description,
        location = location,
        status = status.name,
        startTime = startTime,
        endTime = endTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

