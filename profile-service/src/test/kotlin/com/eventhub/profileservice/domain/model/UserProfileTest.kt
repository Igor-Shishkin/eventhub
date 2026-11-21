package com.eventhub.profileservice.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserProfileTest {

    @Test
    fun `should create minimal profile`() {
        val profile = UserProfile(userId = "user-1")

        assertEquals("user-1", profile.userId)
        assertNull(profile.displayName)
        assertEquals(true, profile.emailNotificationsEnabled)
    }

    @Test
    fun `should update profile and trim blank values`() {
        val profile = UserProfile(userId = "user-1")

        val updated = profile.update(
            displayName = "  Ada Lovelace  ",
            bio = "  Organizer  ",
            locale = " en-GB ",
            timezone = " Europe/London ",
            emailNotificationsEnabled = false
        )

        assertEquals("Ada Lovelace", updated.displayName)
        assertEquals("Organizer", updated.bio)
        assertEquals("en-GB", updated.locale)
        assertEquals("Europe/London", updated.timezone)
        assertEquals(false, updated.emailNotificationsEnabled)
    }

    @Test
    fun `should reject a blank user id`() {
        assertThrows<IllegalArgumentException> {
            UserProfile(userId = " ")
        }
    }
}
