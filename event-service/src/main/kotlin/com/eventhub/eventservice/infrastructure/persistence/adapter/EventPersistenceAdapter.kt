package com.eventhub.eventservice.infrastructure.persistence.adapter

import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import com.eventhub.eventservice.infrastructure.persistence.repository.SpringDataEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EventPersistenceAdapter(
    private val repository: SpringDataEventRepository
) : EventRepositoryPort {

    override suspend fun save(event: Event): Event {
        val entity = event.toEntity()
        val savedEntity = repository.save(entity)
        return savedEntity.toDomain()
    }

    override suspend fun findById(id: UUID): Event? {
        return repository.findById(id)?.toDomain()
    }

    override fun findAll(): Flow<Event> {
        return repository.findAll().map { it.toDomain() }
    }

    override suspend fun deleteById(id: UUID) {
        repository.deleteById(id)
    }
}

