package io.github.giuliodalbono.swapit.dto

import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class SwapProposalDto(
    val id: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val presentationLetter: String?,
    val status: SwapProposalStatus,
    val version: Long,
    val creationTime: LocalDateTime,
    val lastUpdate: LocalDateTime,
    val skillOfferedId: Long?,
    val skillRequestedId: Long?,
    val requestUserUid: String?,
    val offerUserUid: String?
)

data class CreateSwapProposalRequest(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val presentationLetter: String?,
    val status: SwapProposalStatus,
    val skillOfferedId: Long,
    val skillRequestedId: Long,
    val requestUserUid: String,
    val offerUserUid: String
)

data class UpdateSwapProposalRequest(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val presentationLetter: String?,
    val status: SwapProposalStatus,
    val skillOfferedId: Long,
    val skillRequestedId: Long,
    val requestUserUid: String,
    val offerUserUid: String
)
