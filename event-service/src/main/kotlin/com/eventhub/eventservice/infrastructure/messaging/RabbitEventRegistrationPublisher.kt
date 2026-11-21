package com.eventhub.eventservice.infrastructure.messaging

import com.eventhub.eventservice.application.port.out.EventRegistrationCreatedMessage
import com.eventhub.eventservice.application.port.out.EventRegistrationEventPublisherPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class RabbitEventRegistrationPublisher(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${app.messaging.rabbitmq.exchange}") private val exchangeName: String,
    @Value("\${app.messaging.rabbitmq.event-registration-routing-key}") private val routingKey: String
) : EventRegistrationEventPublisherPort {

    override suspend fun publish(message: EventRegistrationCreatedMessage) {
        val payload = objectMapper.writeValueAsString(message)
        withContext(Dispatchers.IO) {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, payload)
        }
    }
}
