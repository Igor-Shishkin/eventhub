package com.eventhub.notificationservice.application

import com.eventhub.notificationservice.messaging.EventRegistrationCreatedMessage
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.BeforeEach
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

class RegistrationNotificationHandlerTest {

    private val notificationSender: NotificationSenderPort = mockk(relaxed = true)
    private val objectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()
    private val handler = RegistrationNotificationHandler(objectMapper, notificationSender)

    @BeforeEach
    fun setUp() {
        io.mockk.clearMocks(notificationSender)
    }

    @Test
    fun `should send registration confirmation for valid message`() {
        val message = EventRegistrationCreatedMessage(
            eventId = UUID.randomUUID(),
            registrationId = UUID.randomUUID(),
            userId = "user-1",
            eventTitle = "Cloud Native Meetup",
            eventStartTime = Instant.parse("2026-06-01T18:00:00Z"),
            registeredAt = Instant.parse("2026-05-01T12:00:00Z")
        )

        handler.handle(objectMapper.writeValueAsString(message))

        verify(exactly = 1) { notificationSender.sendRegistrationConfirmation(message) }
    }

    @Test
    fun `should reject malformed message`() {
        assertFailsWith<Exception> {
            handler.handle("not-json")
        }
    }
}
