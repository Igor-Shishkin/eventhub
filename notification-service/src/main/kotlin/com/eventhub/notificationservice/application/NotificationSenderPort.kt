package com.eventhub.notificationservice.application

import com.eventhub.notificationservice.messaging.EventRegistrationCreatedMessage

interface NotificationSenderPort {
    fun sendRegistrationConfirmation(message: EventRegistrationCreatedMessage)
}
