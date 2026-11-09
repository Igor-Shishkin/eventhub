package com.eventhub.eventservice.domain.exception

import java.util.UUID

class RegistrationNotFoundException(eventId: UUID, userId: String) :
    RuntimeException("Registration for event $eventId and user $userId not found")

