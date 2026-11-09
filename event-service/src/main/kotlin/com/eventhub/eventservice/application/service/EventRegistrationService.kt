package com.eventhub.eventservice.application.service

import com.eventhub.eventservice.application.port.`in`.CancelRegistrationCommand
import com.eventhub.eventservice.application.port.`in`.EventRegistrationUseCase
import com.eventhub.eventservice.application.port.`in`.RegisterAttendeeCommand
import com.eventhub.eventservice.domain.exception.EventNotFoundException
import com.eventhub.eventservice.domain.exception.RegistrationNotFoundException
import com.eventhub.eventservice.domain.model.EventRegistration
import com.eventhub.eventservice.domain.model.EventStatus
import com.eventhub.eventservice.domain.model.RegistrationStatus
import com.eventhub.eventservice.domain.port.EventRegistrationRepositoryPort
import com.eventhub.eventservice.domain.port.EventRepositoryPort
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EventRegistrationService(
    private val eventRepository: EventRepositoryPort,
    private val registrationRepository: EventRegistrationRepositoryPort
) : EventRegistrationUseCase {

    override suspend fun registerAttendee(command: RegisterAttendeeCommand): EventRegistration {
        val event = eventRepository.findById(command.eventId)
            ?: throw EventNotFoundException(command.eventId)

        check(event.status == EventStatus.PUBLISHED) {
            "Cannot register for an event that is not PUBLISHED (current status: ${event.status})"
        }

        val existingRegistration = registrationRepository.findByEventIdAndUserId(command.eventId, command.userId)
        val registrationToSave = if (existingRegistration != null) {
            check(existingRegistration.status != RegistrationStatus.REGISTERED) {
                "User '${command.userId}' is already registered for event '${command.eventId}'"
            }
            existingRegistration.reRegister()
        } else {
            EventRegistration(
                eventId = command.eventId,
                userId = command.userId
            )
        }

        return registrationRepository.save(registrationToSave)
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

