package com.eventhub.profileservice.domain.port

import com.eventhub.profileservice.domain.model.UserProfile

interface UserProfileRepositoryPort {
    suspend fun findByUserId(userId: String): UserProfile?
    suspend fun save(profile: UserProfile): UserProfile
}
