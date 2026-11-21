package com.eventhub.notificationservice.application

import com.eventhub.notificationservice.messaging.EventRegistrationCreatedMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LoggingNotificationSender : NotificationSenderPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendRegistrationConfirmation(message: EventRegistrationCreatedMessage) {
        logger.info(
            "Registration confirmation prepared: userId={}, eventId={}, registrationId={}, eventTitle={}, eventStartTime={}",
            message.userId,
            message.eventId,
            message.registrationId,
            message.eventTitle,
            message.eventStartTime
        )
    }
}
