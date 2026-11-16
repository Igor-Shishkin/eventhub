package com.eventhub.profileservice.domain.port

import com.eventhub.profileservice.domain.model.UserProfile

interface UserProfileRepositoryPort {
    suspend fun findByUserId(userId: String): UserProfile?
    suspend fun create(profile: UserProfile): UserProfile
    suspend fun update(profile: UserProfile): UserProfile
}
