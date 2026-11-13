package com.eventhub.profileservice.application.service

import com.eventhub.profileservice.application.port.`in`.UpdateProfileCommand
import com.eventhub.profileservice.domain.model.UserProfile
import com.eventhub.profileservice.domain.port.UserProfileRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserProfileServiceTest {

    private val repository = mockk<UserProfileRepositoryPort>()
    private val service = UserProfileService(repository)

    @Test
    fun `should lazily create profile when it does not exist`() = runTest {
        coEvery { repository.findByUserId("user-1") } returns null
        coEvery { repository.save(any()) } answers { firstArg() }

        val result = service.getOrCreateProfile("user-1")

        assertEquals("user-1", result.userId)
        assertEquals(null, result.displayName)
        coVerify(exactly = 1) { repository.save(match { it.userId == "user-1" }) }
    }

    @Test
    fun `should return existing profile without saving`() = runTest {
        val existing = UserProfile(userId = "user-1", displayName = "Ada")
        coEvery { repository.findByUserId("user-1") } returns existing

        val result = service.getOrCreateProfile("user-1")

        assertEquals(existing, result)
        coVerify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should update existing profile`() = runTest {
        val existing = UserProfile(userId = "user-1")
        coEvery { repository.findByUserId("user-1") } returns existing
        coEvery { repository.save(any()) } answers { firstArg() }

        val result = service.updateProfile(
            UpdateProfileCommand(
                userId = "user-1",
                displayName = "Ada",
                bio = "Organizer",
                locale = "en-GB",
                timezone = "Europe/London",
                emailNotificationsEnabled = false
            )
        )

        assertEquals("Ada", result.displayName)
        assertEquals("Organizer", result.bio)
        assertEquals(false, result.emailNotificationsEnabled)
        coVerify(exactly = 1) { repository.save(match { it.displayName == "Ada" }) }
    }
}
