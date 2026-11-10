package com.eventhub.eventservice.infrastructure.rest

import com.eventhub.eventservice.application.port.`in`.EventUseCase
import com.eventhub.eventservice.infrastructure.rest.dto.CreateEventRequest
import com.eventhub.eventservice.infrastructure.rest.dto.EventResponse
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val eventUseCase: EventUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createEvent(
        @Valid @RequestBody request: CreateEventRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): EventResponse {
        val event = eventUseCase.createEvent(
            request.toCommand(requireNotNull(jwt.subject))
        )
        return EventResponse.from(event)
    }

    @GetMapping
    fun getAllEvents(): Flow<EventResponse> {
        return eventUseCase.getAllEvents().map { EventResponse.from(it) }
    }

    @GetMapping("/{id}")
    suspend fun getEventById(@PathVariable id: UUID): EventResponse {
        val event = eventUseCase.getEventById(id)
        return EventResponse.from(event)
    }

    @PostMapping("/{id}/publish")
    suspend fun publishEvent(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): EventResponse {
        val event = eventUseCase.publishEvent(id, requireNotNull(jwt.subject))
        return EventResponse.from(event)
    }

    @PostMapping("/{id}/cancel")
    suspend fun cancelEvent(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): EventResponse {
        val event = eventUseCase.cancelEvent(id, requireNotNull(jwt.subject))
        return EventResponse.from(event)
    }
}
