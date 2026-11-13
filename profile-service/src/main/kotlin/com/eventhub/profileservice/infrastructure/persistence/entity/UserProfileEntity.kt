package com.eventhub.profileservice.infrastructure.persistence.entity

import com.eventhub.profileservice.domain.model.UserProfile
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_profiles")
data class UserProfileEntity(
    @Id
    @Column("user_id")
    val userId: String,
    @Column("display_name")
    val displayName: String?,
    val bio: String?,
    val locale: String?,
    val timezone: String?,
    @Column("email_notifications_enabled")
    val emailNotificationsEnabled: Boolean,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant
) {
    fun toDomain(): UserProfile = UserProfile(
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
