package com.eventhub.eventservice.domain.port

import com.eventhub.eventservice.domain.model.EventRegistration
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface EventRegistrationRepositoryPort {
    suspend fun save(registration: EventRegistration): EventRegistration
    suspend fun findById(id: UUID): EventRegistration?
    suspend fun findByEventIdAndUserId(eventId: UUID, userId: String): EventRegistration?
    fun findByEventId(eventId: UUID): Flow<EventRegistration>
    fun findByUserId(userId: String): Flow<EventRegistration>
}

