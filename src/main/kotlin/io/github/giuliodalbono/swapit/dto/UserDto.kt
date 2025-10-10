package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import java.time.LocalDateTime

@Schema(description = "User information and profile data")
data class UserDto(
    @Schema(description = "Unique identifier for the user", example = "user_12345")
    val uid: String,

    @Email
    @Schema(description = "User's email address", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "Display name or username", example = "john_doe")
    val username: String,

    @Schema(description = "User's profile picture")
    val profilePicture: ByteArray?,

    @Schema(description = "Skills the user is interested in")
    val skillDesired: Set<String>?,

    @Schema(description = "Skills the user is willing to share his knowledge")
    val skillOffered: Set<String>?,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the user was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to user data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime
)

@Schema(description = "Request payload for creating a new user")
data class CreateUserRequest(
    @Schema(description = "Unique identifier for the user", example = "user_12345", required = true)
    val uid: String,

    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    val email: String,

    @Schema(description = "Display name or username", example = "john_doe", required = true)
    val username: String,

    @Schema(description = "User's profile picture", required = false)
    val profilePicture: String?
)

@Schema(description = "Request payload for updating an existing user")
data class UpdateUserRequest(
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    val email: String,

    @Schema(description = "Display name or username", example = "john_doe", required = true)
    val username: String,

    @Schema(description = "User's profile picture", required = false)
    val profilePicture: String?
)
