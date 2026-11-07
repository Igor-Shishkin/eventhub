package com.eventhub.eventservice.infrastructure.persistence.adapter

import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.port.EventRegistrationRepositoryPort
import com.eventhub.eventservice.infrastructure.persistence.repository.SpringDataEventRegistrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EventRegistrationPersistenceAdapter(
    private val repository: SpringDataEventRegistrationRepository
) : EventRegistrationRepositoryPort {

    override suspend fun save(registration: EventRegistration): EventRegistration {
        val entity = registration.toEntity()
        val savedEntity = repository.save(entity)
        return savedEntity.toDomain()
    }

    override suspend fun findById(id: UUID): EventRegistration? {
        return repository.findById(id)?.toDomain()
    }

    override suspend fun findByEventIdAndUserId(eventId: UUID, userId: String): EventRegistration? {
        return repository.findByEventIdAndUserId(eventId, userId)?.toDomain()
    }

    override fun findByEventId(eventId: UUID): Flow<EventRegistration> {
        return repository.findByEventId(eventId).map { it.toDomain() }
    }

    override fun findByUserId(userId: String): Flow<EventRegistration> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }
}

