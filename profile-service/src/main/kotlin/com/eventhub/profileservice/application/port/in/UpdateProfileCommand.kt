package com.eventhub.profileservice.application.port.`in`

data class UpdateProfileCommand(
    val userId: String,
    val displayName: String?,
    val bio: String?,
    val locale: String?,
    val timezone: String?,
    val emailNotificationsEnabled: Boolean
)
