package com.eventhub.notificationservice.messaging

import com.eventhub.notificationservice.application.RegistrationNotificationHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RabbitNotificationListener(
    private val notificationHandler: RegistrationNotificationHandler
) {
    @RabbitListener(queues = ["\${app.messaging.rabbitmq.queue}"])
    fun onEventRegistrationCreated(payload: String) {
        notificationHandler.handle(payload)
    }
}
