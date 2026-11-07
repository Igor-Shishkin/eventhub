package com.eventhub.eventservice.application.port.`in`

import com.eventhub.eventservice.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

data class CreateEventCommand(
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null
)

interface EventUseCase {
    suspend fun createEvent(command: CreateEventCommand): Event
    suspend fun getEventById(id: UUID): Event
    fun getAllEvents(): Flow<Event>
    suspend fun publishEvent(id: UUID): Event
    suspend fun cancelEvent(id: UUID): Event
}

