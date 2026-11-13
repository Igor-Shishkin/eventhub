package com.eventhub.profileservice.application.service

import com.eventhub.profileservice.application.port.`in`.UpdateProfileCommand
import com.eventhub.profileservice.application.port.`in`.UserProfileUseCase
import com.eventhub.profileservice.domain.model.UserProfile
import com.eventhub.profileservice.domain.port.UserProfileRepositoryPort
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val profileRepository: UserProfileRepositoryPort
) : UserProfileUseCase {

    override suspend fun getOrCreateProfile(userId: String): UserProfile {
        return profileRepository.findByUserId(userId)
            ?: profileRepository.save(UserProfile(userId = userId))
    }

    override suspend fun updateProfile(command: UpdateProfileCommand): UserProfile {
        val current = getOrCreateProfile(command.userId)
        return profileRepository.save(
            current.update(
                displayName = command.displayName,
                bio = command.bio,
                locale = command.locale,
                timezone = command.timezone,
                emailNotificationsEnabled = command.emailNotificationsEnabled
            )
        )
    }
}
