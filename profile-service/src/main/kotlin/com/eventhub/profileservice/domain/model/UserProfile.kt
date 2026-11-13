package com.eventhub.profileservice.domain.model

import com.eventhub.profileservice.infrastructure.persistence.entity.UserProfileEntity
import java.time.Instant

private const val MAX_DISPLAY_NAME_LENGTH = 120
private const val MAX_BIO_LENGTH = 2_000
private const val MAX_LOCALE_LENGTH = 32
private const val MAX_TIMEZONE_LENGTH = 64

data class UserProfile(
    val userId: String,
    val displayName: String? = null,
    val bio: String? = null,
    val locale: String? = null,
    val timezone: String? = null,
    val emailNotificationsEnabled: Boolean = true,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    init {
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        require(displayName == null || displayName.length <= MAX_DISPLAY_NAME_LENGTH) {
            "Display name cannot exceed $MAX_DISPLAY_NAME_LENGTH characters"
        }
        require(bio == null || bio.length <= MAX_BIO_LENGTH) {
            "Bio cannot exceed $MAX_BIO_LENGTH characters"
        }
        require(locale == null || locale.length <= MAX_LOCALE_LENGTH) {
            "Locale cannot exceed $MAX_LOCALE_LENGTH characters"
        }
        require(timezone == null || timezone.length <= MAX_TIMEZONE_LENGTH) {
            "Timezone cannot exceed $MAX_TIMEZONE_LENGTH characters"
        }
    }

    fun update(
        displayName: String?,
        bio: String?,
        locale: String?,
        timezone: String?,
        emailNotificationsEnabled: Boolean
    ): UserProfile = copy(
        displayName = displayName?.trim()?.takeIf { it.isNotEmpty() },
        bio = bio?.trim()?.takeIf { it.isNotEmpty() },
        locale = locale?.trim()?.takeIf { it.isNotEmpty() },
        timezone = timezone?.trim()?.takeIf { it.isNotEmpty() },
        emailNotificationsEnabled = emailNotificationsEnabled,
        updatedAt = Instant.now()
    )

    fun toEntity(): UserProfileEntity = UserProfileEntity(
        userId = userId,
        displayName = displayName,
        bio = bio,
        locale = locale,
        timezone = timezone,
        emailNotificationsEnabled = emailNotificationsEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
