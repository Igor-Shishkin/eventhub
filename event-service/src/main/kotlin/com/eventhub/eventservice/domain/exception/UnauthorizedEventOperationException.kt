package com.eventhub.eventservice.domain.exception

import java.util.UUID

class UnauthorizedEventOperationException(eventId: UUID, userId: String) :
    RuntimeException("User $userId is not allowed to modify event $eventId")
