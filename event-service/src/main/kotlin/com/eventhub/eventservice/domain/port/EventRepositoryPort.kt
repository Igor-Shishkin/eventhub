package com.eventhub.eventservice.domain.port

import com.eventhub.eventservice.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface EventRepositoryPort {
    suspend fun save(event: Event): Event
    suspend fun findById(id: UUID): Event?
    fun findAll(): Flow<Event>
    suspend fun deleteById(id: UUID)
}

