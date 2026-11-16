package com.eventhub.profileservice.infrastructure.rest

import com.eventhub.profileservice.application.port.`in`.UserProfileUseCase
import com.eventhub.profileservice.infrastructure.rest.dto.UpdateUserProfileRequest
import com.eventhub.profileservice.infrastructure.rest.dto.UserProfileResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/me")
class UserProfileController(
    private val userProfileUseCase: UserProfileUseCase
) {

    @GetMapping
    suspend fun getMyProfile(@AuthenticationPrincipal jwt: Jwt): UserProfileResponse {
        val profile = userProfileUseCase.getOrCreateProfile(requireNotNull(jwt.subject))
        return UserProfileResponse.from(profile)
    }

    @PutMapping
    suspend fun updateMyProfile(
        @Valid @RequestBody request: UpdateUserProfileRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): UserProfileResponse {
        val profile = userProfileUseCase.updateProfile(request.toCommand(requireNotNull(jwt.subject)))
        return UserProfileResponse.from(profile)
    }
}
