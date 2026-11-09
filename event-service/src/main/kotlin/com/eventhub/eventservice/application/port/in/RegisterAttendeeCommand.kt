package com.eventhub.eventservice.application.port.`in`

import java.util.UUID

data class RegisterAttendeeCommand(
    val eventId: UUID,
    val userId: String
)

