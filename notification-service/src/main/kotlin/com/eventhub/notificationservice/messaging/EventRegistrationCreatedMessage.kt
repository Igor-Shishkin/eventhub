package com.eventhub.notificationservice.messaging

import java.time.Instant
import java.util.UUID

data class EventRegistrationCreatedMessage(
    val eventId: UUID,
    val registrationId: UUID,
    val userId: String,
    val eventTitle: String,
    val eventStartTime: Instant,
    val registeredAt: Instant
)
