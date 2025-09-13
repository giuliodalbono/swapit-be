package io.github.giuliodalbono.swapit.dto

import java.time.LocalDateTime

data class UserDto(
    val uid: String,
    val email: String,
    val username: String,
    val version: Long,
    val creationTime: LocalDateTime,
    val lastUpdate: LocalDateTime
)

data class CreateUserRequest(
    val uid: String,
    val email: String,
    val username: String
)

data class UpdateUserRequest(
    val email: String,
    val username: String
)
