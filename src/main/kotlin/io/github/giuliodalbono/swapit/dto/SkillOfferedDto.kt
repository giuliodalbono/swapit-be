package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Skill offered by a user")
data class SkillOfferedDto(
    @Schema(description = "Unique identifier for the skill offered", example = "1")
    val id: Long,

    @Schema(description = "User who offers this skill", example = "user123")
    val userUid: String,

    @Schema(description = "Skill information")
    val skill: SkillDto,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the skill was marked as offered")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to skill offered data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime
)

@Schema(description = "Request payload for creating a new skill offered")
data class CreateSkillOfferedRequest(
    @Schema(description = "User who offers this skill", example = "user123", required = true)
    val userUid: String,

    @Schema(description = "Skill ID to be offered", example = "1", required = true)
    val skillId: Long
)

@Schema(description = "Request payload for updating an existing skill offered")
data class UpdateSkillOfferedRequest(
    @Schema(description = "User who offers this skill", example = "user123", required = true)
    val userUid: String,

    @Schema(description = "Skill ID to be offered", example = "1", required = true)
    val skillId: Long
)
