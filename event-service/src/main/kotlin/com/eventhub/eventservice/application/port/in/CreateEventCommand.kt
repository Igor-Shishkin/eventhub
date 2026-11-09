package com.eventhub.eventservice.application.port.`in`

import java.time.Instant

data class CreateEventCommand(
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val organizerId: String,
    val startTime: Instant,
    val endTime: Instant? = null
)

