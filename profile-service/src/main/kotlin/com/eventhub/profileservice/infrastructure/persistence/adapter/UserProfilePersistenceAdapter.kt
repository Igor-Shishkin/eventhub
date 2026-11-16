package com.eventhub.profileservice.infrastructure.persistence.adapter

import com.eventhub.profileservice.domain.model.UserProfile
import com.eventhub.profileservice.domain.port.UserProfileRepositoryPort
import com.eventhub.profileservice.infrastructure.persistence.repository.SpringDataUserProfileRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component

@Component
class UserProfilePersistenceAdapter(
    private val repository: SpringDataUserProfileRepository,
    private val databaseClient: DatabaseClient
) : UserProfileRepositoryPort {

    override suspend fun findByUserId(userId: String): UserProfile? =
        repository.findById(userId)?.toDomain()

    override suspend fun save(profile: UserProfile): UserProfile {
        databaseClient.sql(
            """
            INSERT INTO user_profiles (
                user_id,
                display_name,
                bio,
                locale,
                timezone,
                email_notifications_enabled,
                created_at,
                updated_at
            )
            VALUES (
                :userId,
                :displayName,
                :bio,
                :locale,
                :timezone,
                :emailNotificationsEnabled,
                :createdAt,
                :updatedAt
            )
            ON CONFLICT (user_id) DO UPDATE SET
                display_name = EXCLUDED.display_name,
                bio = EXCLUDED.bio,
                locale = EXCLUDED.locale,
                timezone = EXCLUDED.timezone,
                email_notifications_enabled = EXCLUDED.email_notifications_enabled,
                updated_at = EXCLUDED.updated_at
            """.trimIndent()
        )
            .bind("userId", profile.userId)
            .bindNullable("displayName", profile.displayName, String::class.java)
            .bindNullable("bio", profile.bio, String::class.java)
            .bindNullable("locale", profile.locale, String::class.java)
            .bindNullable("timezone", profile.timezone, String::class.java)
            .bind("emailNotificationsEnabled", profile.emailNotificationsEnabled)
            .bind("createdAt", profile.createdAt)
            .bind("updatedAt", profile.updatedAt)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return profile
    }

    private fun DatabaseClient.GenericExecuteSpec.bindNullable(
        name: String,
        value: String?,
        type: Class<String>
    ): DatabaseClient.GenericExecuteSpec =
        if (value == null) bindNull(name, type) else bind(name, value)
}
