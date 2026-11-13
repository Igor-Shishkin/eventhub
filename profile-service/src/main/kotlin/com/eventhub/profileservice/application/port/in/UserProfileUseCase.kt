package com.eventhub.profileservice.application.port.`in`

import com.eventhub.profileservice.domain.model.UserProfile

interface UserProfileUseCase {
    suspend fun getOrCreateProfile(userId: String): UserProfile
    suspend fun updateProfile(command: UpdateProfileCommand): UserProfile
}
