package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.model.entity.Feedback
import org.springframework.stereotype.Component

@Component
class FeedbackMapper {

    fun toDto(feedback: Feedback): FeedbackDto {
        return FeedbackDto(
            id = feedback.id!!,
            rating = feedback.rating!!,
            review = feedback.review!!,
            version = feedback.version,
            creationTime = feedback.creationTime!!,
            lastUpdate = feedback.lastUpdate!!,
            reviewerUid = feedback.reviewer?.uid,
            reviewedUid = feedback.reviewed?.uid
        )
    }

    fun toEntity(createRequest: CreateFeedbackRequest): Feedback {
        return Feedback().apply {
            rating = createRequest.rating
            review = createRequest.review
        }
    }

    fun updateEntity(feedback: Feedback, updateRequest: UpdateFeedbackRequest): Feedback {
        feedback.rating = updateRequest.rating
        feedback.review = updateRequest.review
        return feedback
    }
}
