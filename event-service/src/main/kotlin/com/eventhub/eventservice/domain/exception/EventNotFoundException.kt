package com.eventhub.eventservice.domain.exception

import java.util.UUID

class EventNotFoundException(id: UUID) : RuntimeException("Event with id $id not found")

