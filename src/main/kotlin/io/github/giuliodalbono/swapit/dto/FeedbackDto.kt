package io.github.giuliodalbono.swapit.dto

import java.time.LocalDateTime

data class FeedbackDto(
    val id: Long,
    val rating: Long,
    val review: String,
    val version: Long,
    val creationTime: LocalDateTime,
    val lastUpdate: LocalDateTime,
    val reviewerUid: String?,
    val reviewedUid: String?
)

data class CreateFeedbackRequest(
    val rating: Long,
    val review: String,
    val reviewerUid: String,
    val reviewedUid: String
)

data class UpdateFeedbackRequest(
    val rating: Long,
    val review: String,
    val reviewerUid: String,
    val reviewedUid: String
)
