package com.eventhub.eventservice.infrastructure.rest

import com.eventhub.eventservice.application.port.`in`.CancelRegistrationCommand
import com.eventhub.eventservice.application.port.`in`.EventRegistrationUseCase
import com.eventhub.eventservice.application.port.`in`.RegisterAttendeeCommand
import com.eventhub.eventservice.domain.exception.RegistrationNotFoundException
import com.eventhub.eventservice.infrastructure.rest.dto.EventRegistrationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
        @AuthenticationPrincipal jwt: Jwt
    ): EventRegistrationResponse {
        val registration = registrationUseCase.registerAttendee(
            RegisterAttendeeCommand(
                eventId = eventId,
                userId = requireNotNull(jwt.subject)
            )
        )
        return EventRegistrationResponse.from(registration)
    }

    @GetMapping
    fun getRegistrationsForEvent(@PathVariable eventId: UUID): Flow<EventRegistrationResponse> {
        return registrationUseCase.getRegistrationsForEvent(eventId)
            .map { EventRegistrationResponse.from(it) }
    }

    @GetMapping("/me")
    suspend fun getMyRegistration(
        @PathVariable eventId: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): EventRegistrationResponse {
        val userId = requireNotNull(jwt.subject)
        val registration = registrationUseCase.getRegistration(eventId, userId)
            ?: throw RegistrationNotFoundException(eventId, userId)
        return EventRegistrationResponse.from(registration)
    }

    @DeleteMapping("/me")
    suspend fun cancelMyRegistration(
        @PathVariable eventId: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): EventRegistrationResponse {
        val cancelled = registrationUseCase.cancelRegistration(
            CancelRegistrationCommand(
                eventId = eventId,
                userId = requireNotNull(jwt.subject)
            )
        )
        return EventRegistrationResponse.from(cancelled)
    }
}
