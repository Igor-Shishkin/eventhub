package com.eventhub.eventservice.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventTest {

    @Test
    fun `should create valid draft event`() {
        val now = Instant.now()
        val event = Event(
            title = "Tech Meetup",
            description = "A meetup for developers",
            location = "Warsaw",
            startTime = now.plus(1, ChronoUnit.DAYS),
            endTime = now.plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS)
        )

        assertEquals("Tech Meetup", event.title)
        assertEquals(EventStatus.DRAFT, event.status)
        assertNotNull(event.createdAt)
    }

    @Test
    fun `should throw exception when title is blank`() {
        assertThrows<IllegalArgumentException> {
            Event(title = "   ")
        }
    }

    @Test
    fun `should throw exception when end time is before start time`() {
        val now = Instant.now()
        assertThrows<IllegalArgumentException> {
            Event(
                title = "Tech Meetup",
                startTime = now.plus(2, ChronoUnit.HOURS),
                endTime = now.plus(1, ChronoUnit.HOURS)
            )
        }
    }

    @Test
    fun `should publish draft event`() {
        val event = Event(title = "Tech Meetup")
        val published = event.publish()

        assertEquals(EventStatus.PUBLISHED, published.status)
    }

    @Test
    fun `should fail to publish when event is already published`() {
        val event = Event(title = "Tech Meetup", status = EventStatus.PUBLISHED)

        assertThrows<IllegalStateException> {
            event.publish()
        }
    }

    @Test
    fun `should cancel event`() {
        val event = Event(title = "Tech Meetup")
        val cancelled = event.cancel()

        assertEquals(EventStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `should fail to cancel already cancelled event`() {
        val event = Event(title = "Tech Meetup", status = EventStatus.CANCELLED)

        assertThrows<IllegalStateException> {
            event.cancel()
        }
    }
}

