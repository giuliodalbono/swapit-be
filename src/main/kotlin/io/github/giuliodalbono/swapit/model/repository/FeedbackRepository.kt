package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository: JpaRepository<Feedback, Long> {
    fun findByReviewerUid(reviewerUid: String): List<Feedback>
    fun findByReviewedUid(reviewedUid: String): List<Feedback>
}