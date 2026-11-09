package com.eventhub.eventservice.infrastructure.rest

import com.eventhub.eventservice.application.port.`in`.CancelRegistrationCommand
import com.eventhub.eventservice.application.port.`in`.EventRegistrationUseCase
import com.eventhub.eventservice.application.port.`in`.RegisterAttendeeCommand
import com.eventhub.eventservice.domain.exception.RegistrationNotFoundException
import com.eventhub.eventservice.infrastructure.rest.dto.EventRegistrationResponse
import com.eventhub.eventservice.infrastructure.rest.dto.RegisterAttendeeRequest
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/events/{eventId}/registrations")
class EventRegistrationController(
    private val registrationUseCase: EventRegistrationUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun registerAttendee(
        @PathVariable eventId: UUID,
        @Valid @RequestBody request: RegisterAttendeeRequest
    ): EventRegistrationResponse {
        val registration = registrationUseCase.registerAttendee(
            RegisterAttendeeCommand(
                eventId = eventId,
                userId = request.userId!!
            )
        )
        return EventRegistrationResponse.from(registration)
    }

    @GetMapping
    fun getRegistrationsForEvent(@PathVariable eventId: UUID): Flow<EventRegistrationResponse> {
        return registrationUseCase.getRegistrationsForEvent(eventId)
            .map { EventRegistrationResponse.from(it) }
    }

    @GetMapping("/{userId}")
    suspend fun getRegistration(
        @PathVariable eventId: UUID,
        @PathVariable userId: String
    ): EventRegistrationResponse {
        val registration = registrationUseCase.getRegistration(eventId, userId)
            ?: throw RegistrationNotFoundException(eventId, userId)
        return EventRegistrationResponse.from(registration)
    }

    @PostMapping("/{userId}/cancel")
    suspend fun cancelRegistration(
        @PathVariable eventId: UUID,
        @PathVariable userId: String
    ): EventRegistrationResponse {
        val cancelled = registrationUseCase.cancelRegistration(
            CancelRegistrationCommand(
                eventId = eventId,
                userId = userId
            )
        )
        return EventRegistrationResponse.from(cancelled)
    }
}

