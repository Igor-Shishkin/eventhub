package com.eventhub.eventservice.infrastructure.persistence.repository

import com.eventhub.eventservice.infrastructure.persistence.entity.EventEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface SpringDataEventRepository : CoroutineCrudRepository<EventEntity, UUID>

