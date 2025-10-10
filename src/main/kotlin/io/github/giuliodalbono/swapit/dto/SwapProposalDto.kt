package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Schema(description = "Swap proposal information for skill exchange")
data class SwapProposalDto(
    @Schema(description = "Unique identifier for the swap proposal", example = "1")
    val id: Long,

    @Schema(description = "Date when the skill swap is scheduled", example = "2024-12-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @Schema(description = "Start time of the skill swap session", example = "14:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    val startTime: LocalTime,

    @Schema(description = "End time of the skill swap session", example = "16:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    val endTime: LocalTime,

    @Schema(description = "Personal message or introduction from the proposer", example = "I'm excited to share my Java expertise and learn about React!")
    val presentationLetter: String?,

    @Schema(description = "Current status of the swap proposal", example = "PENDING")
    val status: SwapProposalStatus,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the proposal was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to proposal data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime,

    @Schema(description = "ID of the skill being offered in exchange", example = "1")
    val skillOfferedId: Long,

    @Schema(description = "ID of the skill being requested in return", example = "2")
    val skillRequestedId: Long,

    @Schema(description = "UID of the user who initiated the proposal", example = "user_12345")
    val requestUserUid: String,

    @Schema(description = "UID of the user who is being proposed to", example = "user_67890")
    val offerUserUid: String
)

@Schema(description = "Request payload for creating a new swap proposal")
data class CreateSwapProposalRequest(
    @Schema(description = "Date when the skill swap is scheduled", example = "2024-12-15", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @Schema(description = "Start time of the skill swap session", example = "14:00:00", required = true)
    @JsonFormat(pattern = "HH:mm:ss")
    val startTime: LocalTime,

    @Schema(description = "End time of the skill swap session", example = "16:00:00", required = true)
    @JsonFormat(pattern = "HH:mm:ss")
    val endTime: LocalTime,

    @Schema(description = "Personal message or introduction from the proposer", example = "I'm excited to share my Java expertise and learn about React!")
    val presentationLetter: String?,

    @Schema(description = "Initial status of the swap proposal", example = "PENDING", required = true)
    val status: SwapProposalStatus,

    @Schema(description = "ID of the skill being offered in exchange", example = "1", required = true)
    val skillOfferedId: Long,

    @Schema(description = "ID of the skill being requested in return", example = "2", required = true)
    val skillRequestedId: Long,

    @Schema(description = "UID of the user who initiated the proposal", example = "user_12345", required = true)
    val requestUserUid: String,

    @Schema(description = "UID of the user who is being proposed to", example = "user_67890", required = true)
    val offerUserUid: String
)

@Schema(description = "Request payload for updating an existing swap proposal")
data class UpdateSwapProposalRequest(
    @Schema(description = "Date when the skill swap is scheduled", example = "2024-12-15", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @Schema(description = "Start time of the skill swap session", example = "14:00:00", required = true)
    @JsonFormat(pattern = "HH:mm:ss")
    val startTime: LocalTime,

    @Schema(description = "End time of the skill swap session", example = "16:00:00", required = true)
    @JsonFormat(pattern = "HH:mm:ss")
    val endTime: LocalTime,

    @Schema(description = "Personal message or introduction from the proposer", example = "I'm excited to share my Java expertise and learn about React!")
    val presentationLetter: String?,

    @Schema(description = "Updated status of the swap proposal", example = "ACCEPTED", required = true)
    val status: SwapProposalStatus,

    @Schema(description = "ID of the skill being offered in exchange", example = "1", required = true)
    val skillOfferedId: Long,

    @Schema(description = "ID of the skill being requested in return", example = "2", required = true)
    val skillRequestedId: Long,

    @Schema(description = "UID of the user who initiated the proposal", example = "user_12345", required = true)
    val requestUserUid: String,

    @Schema(description = "UID of the user who is being proposed to", example = "user_67890", required = true)
    val offerUserUid: String
)
