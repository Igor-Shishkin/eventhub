package com.eventhub.profileservice.infrastructure.persistence.adapter

import com.eventhub.profileservice.domain.model.UserProfile
import com.eventhub.profileservice.domain.port.UserProfileRepositoryPort
import com.eventhub.profileservice.infrastructure.persistence.repository.SpringDataUserProfileRepository
import org.springframework.stereotype.Component

@Component
class UserProfilePersistenceAdapter(
    private val repository: SpringDataUserProfileRepository
) : UserProfileRepositoryPort {

    override suspend fun findByUserId(userId: String): UserProfile? =
        repository.findById(userId)?.toDomain()

    override suspend fun create(profile: UserProfile): UserProfile =
        repository.save(profile.toEntity(isNew = true)).toDomain()

    override suspend fun update(profile: UserProfile): UserProfile =
        repository.save(profile.toEntity()).toDomain()
}
