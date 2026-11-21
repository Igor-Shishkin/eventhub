package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CancelRegistrationCommand
import com.eventhub.eventservice.application.port.`in`.EventRegistrationUseCase
import com.eventhub.eventservice.application.port.`in`.RegisterAttendeeCommand
import com.eventhub.eventservice.application.port.out.EventRegistrationCreatedMessage
import com.eventhub.eventservice.application.port.out.EventRegistrationEventPublisherPort
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.exception.RegistrationNotFoundException
import com.eventhub.eventservice.domain.model.Event
import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.domain.port.EventRegistrationRepositoryPort
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EventRegistrationService(
    private val eventRepository: EventRepositoryPort,
    private val registrationRepository: EventRegistrationRepositoryPort,
    private val eventPublisher: EventRegistrationEventPublisherPort
) : EventRegistrationUseCase {

    override suspend fun registerAttendee(command: RegisterAttendeeCommand): EventRegistration {
        val event = findPublishedEvent(command.eventId)
        val registration = prepareRegistration(command)
        val savedRegistration = registrationRepository.save(registration)

        publishRegistrationCreated(event, savedRegistration)

        return savedRegistration
    }

    private suspend fun findPublishedEvent(eventId: UUID) =
        eventRepository.findById(eventId)
            ?.also { event ->
                check(event.status == EventStatus.PUBLISHED) {
                    "Cannot register for an event that is not PUBLISHED (current status: ${event.status})"
                }
            }
            ?: throw EventNotFoundException(eventId)

    private suspend fun prepareRegistration(command: RegisterAttendeeCommand): EventRegistration {
        val existing = registrationRepository.findByEventIdAndUserId(command.eventId, command.userId)
        return EventRegistration.createOrReRegister(
            eventId = command.eventId,
            userId = command.userId,
            existing = existing
        )
    }

    private suspend fun publishRegistrationCreated(
        event: Event,
        registration: EventRegistration
    ) {
        eventPublisher.publish(
            EventRegistrationCreatedMessage(
                eventId = requireNotNull(event.id),
                registrationId = requireNotNull(registration.id),
                userId = registration.userId,
                eventTitle = event.title,
                eventStartTime = event.startTime,
                registeredAt = registration.registeredAt
            )
        )
    }

    override suspend fun cancelRegistration(command: CancelRegistrationCommand): EventRegistration {
        val registration = registrationRepository.findByEventIdAndUserId(command.eventId, command.userId)
            ?: throw RegistrationNotFoundException(command.eventId, command.userId)

        val cancelled = registration.cancel()
        return registrationRepository.save(cancelled)
    }

    override fun getRegistrationsForEvent(eventId: UUID): Flow<EventRegistration> {
        return registrationRepository.findByEventId(eventId)
    }

    override suspend fun getRegistration(eventId: UUID, userId: String): EventRegistration? {
        return registrationRepository.findByEventIdAndUserId(eventId, userId)
    }

    override fun getRegistrationsForUser(userId: String): Flow<EventRegistration> {
        return registrationRepository.findByUserId(userId)
    }
}

