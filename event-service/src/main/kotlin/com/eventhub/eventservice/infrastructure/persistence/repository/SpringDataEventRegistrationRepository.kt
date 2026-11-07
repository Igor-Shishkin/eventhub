package com.eventhub.eventservice.infrastructure.persistence.repository

import com.eventhub.eventservice.infrastructure.persistence.entity.EventRegistrationEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface SpringDataEventRegistrationRepository : CoroutineCrudRepository<EventRegistrationEntity, UUID> {
    suspend fun findByEventIdAndUserId(eventId: UUID, userId: String): EventRegistrationEntity?
    fun findByEventId(eventId: UUID): Flow<EventRegistrationEntity>
    fun findByUserId(userId: String): Flow<EventRegistrationEntity>
}

