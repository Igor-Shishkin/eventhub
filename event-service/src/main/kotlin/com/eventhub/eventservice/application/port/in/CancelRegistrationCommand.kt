package com.eventhub.eventservice.application.port.`in`

import java.util.UUID

data class CancelRegistrationCommand(
    val eventId: UUID,
    val userId: String
)

