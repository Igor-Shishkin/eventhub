package com.eventhub.profileservice.infrastructure.rest.dto

import com.eventhub.profileservice.application.port.`in`.UpdateProfileCommand
import com.eventhub.profileservice.domain.model.UserProfile
import jakarta.validation.constraints.Size
import java.time.Instant

 data class UpdateUserProfileRequest(
    @field:Size(max = 120, message = "Display name cannot exceed 120 characters")
    val displayName: String? = null,
    @field:Size(max = 2_000, message = "Bio cannot exceed 2000 characters")
    val bio: String? = null,
    @field:Size(max = 32, message = "Locale cannot exceed 32 characters")
    val locale: String? = null,
    @field:Size(max = 64, message = "Timezone cannot exceed 64 characters")
    val timezone: String? = null,
    val emailNotificationsEnabled: Boolean = true
) {
    fun toCommand(userId: String): UpdateProfileCommand = UpdateProfileCommand(
        userId = userId,
        displayName = displayName,
        bio = bio,
        locale = locale,
        timezone = timezone,
        emailNotificationsEnabled = emailNotificationsEnabled
    )
}

data class UserProfileResponse(
    val userId: String,
    val displayName: String?,
    val bio: String?,
    val locale: String?,
    val timezone: String?,
    val emailNotificationsEnabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(profile: UserProfile): UserProfileResponse = UserProfileResponse(
            userId = profile.userId,
            displayName = profile.displayName,
            bio = profile.bio,
            locale = profile.locale,
            timezone = profile.timezone,
            emailNotificationsEnabled = profile.emailNotificationsEnabled,
            createdAt = profile.createdAt,
            updatedAt = profile.updatedAt
        )
    }
}
