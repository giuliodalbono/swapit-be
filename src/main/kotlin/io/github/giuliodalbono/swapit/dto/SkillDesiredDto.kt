package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Skill desired by a user")
data class SkillDesiredDto(
    @Schema(description = "Unique identifier for the skill desired", example = "1")
    val id: Long,

    @Schema(description = "User who desires this skill", example = "user123")
    val userUid: String,

    @Schema(description = "Skill information")
    val skill: SkillDto,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the skill was marked as desired")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to skill desired data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime
)

@Schema(description = "Request payload for creating a new skill desired")
data class CreateSkillDesiredRequest(
    @Schema(description = "User who desires this skill", example = "user123", required = true)
    val userUid: String,

    @Schema(description = "Skill ID to be desired", example = "1", required = true)
    val skillId: Long
)

@Schema(description = "Request payload for updating an existing skill desired")
data class UpdateSkillDesiredRequest(
    @Schema(description = "User who desires this skill", example = "user123", required = true)
    val userUid: String,

    @Schema(description = "Skill ID to be desired", example = "1", required = true)
    val skillId: Long
)
