package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CreateEventCommand
import com.eventhub.eventservice.application.port.`in`.EventUseCase
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.exception.UnauthorizedEventOperationException
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
            organizerId = command.organizerId,
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

    override suspend fun publishEvent(id: UUID, organizerId: String): Event {
        val event = getEventById(id)
        checkOrganizerAccess(event, organizerId)
        val publishedEvent = event.publish()
        return eventRepository.save(publishedEvent)
    }

    override suspend fun cancelEvent(id: UUID, organizerId: String): Event {
        val event = getEventById(id)
        checkOrganizerAccess(event, organizerId)
        val cancelledEvent = event.cancel()
        return eventRepository.save(cancelledEvent)
    }

    private fun checkOrganizerAccess(event: Event, organizerId: String) {
        if (event.organizerId != organizerId) {
            throw UnauthorizedEventOperationException(requireNotNull(event.id), organizerId)
        }
    }
}
