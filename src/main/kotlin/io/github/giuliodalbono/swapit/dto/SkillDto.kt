package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Skill information and metadata")
data class SkillDto(
    @Schema(description = "Unique identifier for the skill", example = "1")
    val id: Long,

    @Schema(description = "Name or label of the skill", example = "Java Programming")
    val label: String,

    @Schema(description = "Additional metadata as key-value pairs", example = "{\"level\": \"intermediate\", \"category\": \"programming\"}")
    val metadata: Map<String, String>? = emptyMap(),

    @Schema(description = "Detailed description of the skill", example = "Proficiency in Java programming language including OOP concepts, collections, and frameworks")
    val description: String?,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the skill was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to skill data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime
)

@Schema(description = "Request payload for creating a new skill")
data class CreateSkillRequest(
    @Schema(description = "Name or label of the skill", example = "Java Programming", required = true)
    val label: String,

    @Schema(description = "Additional metadata as key-value pairs", example = "{\"level\": \"intermediate\", \"category\": \"programming\"}")
    val metadata: Map<String, String>? = emptyMap(),

    @Schema(description = "Detailed description of the skill", example = "Proficiency in Java programming language including OOP concepts, collections, and frameworks")
    val description: String?
)

@Schema(description = "Request payload for updating an existing skill")
data class UpdateSkillRequest(
    @Schema(description = "Name or label of the skill", example = "Java Programming", required = true)
    val label: String,

    @Schema(description = "Additional metadata as key-value pairs", example = "{\"level\": \"intermediate\", \"category\": \"programming\"}", required = true)
    val metadata: Map<String, String>?,

    @Schema(description = "Detailed description of the skill", example = "Proficiency in Java programming language including OOP concepts, collections, and frameworks")
    val description: String?
)
