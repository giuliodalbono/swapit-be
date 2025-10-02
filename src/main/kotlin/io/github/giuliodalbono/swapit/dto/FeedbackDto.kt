package io.github.giuliodalbono.swapit.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Feedback and review information for skill swap sessions")
data class FeedbackDto(
    @Schema(description = "Unique identifier for the feedback", example = "1")
    val id: Long,

    @Schema(description = "Rating score from 1 to 5", example = "5", minimum = "1", maximum = "5")
    val rating: Long,

    @Schema(description = "Textual review and comments about the skill swap experience", example = "Excellent session! John was very knowledgeable and patient. I learned a lot about React hooks.")
    val review: String,

    @Schema(description = "Version number for optimistic locking", example = "1")
    val version: Long,

    @Schema(description = "Timestamp when the feedback was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationTime: LocalDateTime,

    @Schema(description = "Timestamp of the last update to feedback data")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdate: LocalDateTime,

    @Schema(description = "UID of the user who wrote the feedback", example = "user_12345")
    val reviewerUid: String?,

    @Schema(description = "UID of the user who received the feedback", example = "user_67890")
    val reviewedUid: String?
)

@Schema(description = "Request payload for creating new feedback")
data class CreateFeedbackRequest(
    @Schema(description = "Rating score from 1 to 5", example = "5", minimum = "1", maximum = "5", required = true)
    val rating: Long,
    
    @Schema(description = "Textual review and comments about the skill swap experience", example = "Excellent session! John was very knowledgeable and patient. I learned a lot about React hooks.", required = true)
    val review: String,
    
    @Schema(description = "UID of the user who wrote the feedback", example = "user_12345", required = true)
    val reviewerUid: String,
    
    @Schema(description = "UID of the user who received the feedback", example = "user_67890", required = true)
    val reviewedUid: String
)

@Schema(description = "Request payload for updating existing feedback")
data class UpdateFeedbackRequest(
    @Schema(description = "Rating score from 1 to 5", example = "5", minimum = "1", maximum = "5", required = true)
    val rating: Long,

    @Schema(description = "Textual review and comments about the skill swap experience", example = "Excellent session! John was very knowledgeable and patient. I learned a lot about React hooks.", required = true)
    val review: String,

    @Schema(description = "UID of the user who wrote the feedback", example = "user_12345", required = true)
    val reviewerUid: String,

    @Schema(description = "UID of the user who received the feedback", example = "user_67890", required = true)
    val reviewedUid: String
)
