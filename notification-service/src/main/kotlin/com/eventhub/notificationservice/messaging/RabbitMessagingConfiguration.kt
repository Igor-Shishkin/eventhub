package com.eventhub.notificationservice.messaging

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMessagingConfiguration(
    @Value("\${app.messaging.rabbitmq.exchange}") private val exchangeName: String,
    @Value("\${app.messaging.rabbitmq.queue}") private val queueName: String,
    @Value("\${app.messaging.rabbitmq.routing-key}") private val routingKey: String
) {
    @Bean
    fun businessExchange(): TopicExchange = TopicExchange(exchangeName, true, false)

    @Bean
    fun eventRegistrationNotificationQueue(): Queue = Queue(queueName, true)

    @Bean
    fun eventRegistrationNotificationBinding(
        eventRegistrationNotificationQueue: Queue,
        businessExchange: TopicExchange
    ): Binding = BindingBuilder
        .bind(eventRegistrationNotificationQueue)
        .to(businessExchange)
        .with(routingKey)
}
