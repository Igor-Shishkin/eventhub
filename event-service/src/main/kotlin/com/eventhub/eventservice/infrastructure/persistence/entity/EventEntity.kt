package com.eventhub.eventservice.infrastructure.persistence.entity

import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("events")
data class EventEntity(
    @Id
    val id: UUID? = null,
    val title: String,
    val description: String?,
    val location: String?,
    @Column("organizer_id")
    val organizerId: String,
    val status: String,
    @Column("start_time")
    val startTime: Instant,
    @Column("end_time")
    val endTime: Instant?,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant
) {
    fun toDomain(): Event = Event(
        id = id,
        title = title,
        description = description,
        location = location,
        organizerId = organizerId,
        status = EventStatus.valueOf(status),
        startTime = startTime,
        endTime = endTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

