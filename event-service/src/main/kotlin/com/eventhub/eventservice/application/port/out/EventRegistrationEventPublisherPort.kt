package com.eventhub.eventservice.application.port.out

interface EventRegistrationEventPublisherPort {
    suspend fun publish(message: EventRegistrationCreatedMessage)
}
