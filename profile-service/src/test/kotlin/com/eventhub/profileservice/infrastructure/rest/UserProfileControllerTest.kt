package com.eventhub.profileservice.infrastructure.rest

import com.eventhub.profileservice.application.port.`in`.UserProfileUseCase
import com.eventhub.profileservice.domain.model.UserProfile
import com.eventhub.profileservice.infrastructure.rest.dto.UpdateUserProfileRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant
import kotlin.test.assertEquals

class UserProfileControllerTest {

    private val useCase = mockk<UserProfileUseCase>()
    private val controller = UserProfileController(useCase)
    private val profile = UserProfile(
        userId = "user-1",
        displayName = "Ada Lovelace",
        bio = "Organizer",
        locale = "en-GB",
        timezone = "Europe/London",
        emailNotificationsEnabled = false,
        createdAt = Instant.parse("2026-01-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-01-02T10:00:00Z")
    )

    @Test
    fun `should get profile for subject from jwt`() = runTest {
        coEvery { useCase.getOrCreateProfile("user-1") } returns profile

        val response = controller.getMyProfile(jwt("user-1"))

        assertEquals("user-1", response.userId)
        assertEquals("Ada Lovelace", response.displayName)
        coVerify(exactly = 1) { useCase.getOrCreateProfile("user-1") }
    }

    @Test
    fun `should update profile for subject from jwt`() = runTest {
        coEvery { useCase.updateProfile(any()) } returns profile

        val response = controller.updateMyProfile(
            UpdateUserProfileRequest(
                displayName = "Ada Lovelace",
                bio = "Organizer",
                locale = "en-GB",
                timezone = "Europe/London",
                emailNotificationsEnabled = false
            ),
            jwt("user-1")
        )

        assertEquals("Ada Lovelace", response.displayName)
        coVerify(exactly = 1) {
            useCase.updateProfile(match { it.userId == "user-1" && it.displayName == "Ada Lovelace" })
        }
    }

    private fun jwt(subject: String): Jwt = Jwt.withTokenValue("test-token")
        .header("alg", "none")
        .subject(subject)
        .build()
}
