package com.eventhub.eventservice.infrastructure.messaging

import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMessagingConfiguration(
    @Value("\${app.messaging.rabbitmq.exchange}") private val exchangeName: String
) {
    @Bean
    fun businessExchange(): TopicExchange = TopicExchange(exchangeName, true, false)
}
