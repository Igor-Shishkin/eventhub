package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.application.port.`in`.EventUseCase
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepositoryPort
) : EventUseCase {

    override suspend fun createEvent(command: CreateEventCommand): Event {
        val event = Event(
            title = command.title,
            description = command.description,
            location = command.location,
            startTime = command.startTime,
            endTime = command.endTime
        )
        return eventRepository.save(event)
    }

    override suspend fun getEventById(id: UUID): Event {
        return eventRepository.findById(id) ?: throw EventNotFoundException(id)
    }

    override fun getAllEvents(): Flow<Event> {
        return eventRepository.findAll()
    }

    override suspend fun publishEvent(id: UUID): Event {
        val event = getEventById(id)
        val publishedEvent = event.publish()
        return eventRepository.save(publishedEvent)
    }

    override suspend fun cancelEvent(id: UUID): Event {
        val event = getEventById(id)
        val cancelledEvent = event.cancel()
        return eventRepository.save(cancelledEvent)
    }
}

