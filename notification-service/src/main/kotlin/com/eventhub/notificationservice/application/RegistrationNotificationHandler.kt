package com.eventhub.notificationservice.application

import com.eventhub.notificationservice.messaging.EventRegistrationCreatedMessage
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class RegistrationNotificationHandler(
    private val objectMapper: ObjectMapper,
    private val notificationSender: NotificationSenderPort
) {
    fun handle(payload: String) {
        val message = objectMapper.readValue(payload, EventRegistrationCreatedMessage::class.java)
        notificationSender.sendRegistrationConfirmation(message)
    }
}
