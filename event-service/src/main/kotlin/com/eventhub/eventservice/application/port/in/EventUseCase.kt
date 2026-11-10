package com.eventhub.eventservice.application.port.`in`

import com.eventhub.eventservice.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface EventUseCase {
    suspend fun createEvent(command: CreateEventCommand): Event
    suspend fun getEventById(id: UUID): Event
    fun getAllEvents(): Flow<Event>
    suspend fun publishEvent(id: UUID, organizerId: String): Event
    suspend fun cancelEvent(id: UUID, organizerId: String): Event
}


