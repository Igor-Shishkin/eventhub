package com.eventhub.eventservice.infrastructure.rest.dto

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

data class CreateEventRequest(
    @field:NotBlank(message = "Title cannot be blank")
    val title: String,
    val description: String? = null,
    val location: String? = null,
    @field:NotBlank(message = "Organizer ID cannot be blank")
    val organizerId: String,
    @field:NotNull(message = "Start time cannot be null")
    val startTime: Instant?,
    val endTime: Instant? = null
) {
    fun toCommand(): CreateEventCommand = CreateEventCommand(
        title = title,
        description = description,
        location = location,
        organizerId = organizerId,
        startTime = requireNotNull(startTime) { "Start time is required" },
        endTime = endTime
    )
}

data class EventResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val location: String?,
    val organizerId: String,
    val status: EventStatus,
    val startTime: Instant,
    val endTime: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(event: Event): EventResponse = EventResponse(
            id = requireNotNull(event.id) { "Event ID cannot be null when converting to response" },
            title = event.title,
            description = event.description,
            location = event.location,
            organizerId = event.organizerId,
            status = event.status,
            startTime = event.startTime,
            endTime = event.endTime,
            createdAt = event.createdAt,
            updatedAt = event.updatedAt
        )
    }
}

