package com.eventhub.eventservice.application.port.`in`

import com.eventhub.eventservice.domain.model.EventRegistration
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface EventRegistrationUseCase {
    suspend fun registerAttendee(command: RegisterAttendeeCommand): EventRegistration
    suspend fun cancelRegistration(command: CancelRegistrationCommand): EventRegistration
    fun getRegistrationsForEvent(eventId: UUID): Flow<EventRegistration>
    suspend fun getRegistration(eventId: UUID, userId: String): EventRegistration?
    fun getRegistrationsForUser(userId: String): Flow<EventRegistration>
}



