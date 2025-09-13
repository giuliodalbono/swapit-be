package io.github.giuliodalbono.swapit.dto

import java.time.LocalDateTime

data class SkillDto(
    val id: Long,
    val label: String,
    val metadata: Map<String, String>,
    val description: String?,
    val version: Long,
    val creationTime: LocalDateTime,
    val lastUpdate: LocalDateTime
)

data class CreateSkillRequest(
    val label: String,
    val metadata: Map<String, String> = emptyMap(),
    val description: String?
)

data class UpdateSkillRequest(
    val label: String,
    val metadata: Map<String, String>,
    val description: String?
)
