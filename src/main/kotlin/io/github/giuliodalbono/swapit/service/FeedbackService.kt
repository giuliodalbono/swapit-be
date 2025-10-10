package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.mapper.FeedbackMapper
import io.github.giuliodalbono.swapit.model.repository.FeedbackRepository
import io.github.giuliodalbono.swapit.service.producer.FeedbackEventProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class FeedbackService(
    private val userService: UserService,
    private val feedbackMapper: FeedbackMapper,
    private val feedbackRepository: FeedbackRepository,
    private val feedbackEventProducer: FeedbackEventProducer
) {

    fun findAll(): List<FeedbackDto> = feedbackRepository.findAll().map { feedbackMapper.toDto(it) }

    fun findById(id: Long): Optional<FeedbackDto> = feedbackRepository.findById(id).map { feedbackMapper.toDto(it) }

    fun findByReviewer(reviewerUid: String): List<FeedbackDto> = 
        feedbackRepository.findByReviewerUid(reviewerUid).map { feedbackMapper.toDto(it) }

    fun findByReviewed(reviewedUid: String): List<FeedbackDto> = 
        feedbackRepository.findByReviewedUid(reviewedUid).map { feedbackMapper.toDto(it) }

    fun save(createRequest: CreateFeedbackRequest): FeedbackDto {
        val feedback = feedbackMapper.toEntity(createRequest)

        val reviewer = userService.findEntityByUid(createRequest.reviewerUid)
            .orElseThrow { IllegalArgumentException("Reviewer user with uid ${createRequest.reviewerUid} not found") }
        val reviewed = userService.findEntityByUid(createRequest.reviewedUid)
            .orElseThrow { IllegalArgumentException("Reviewed user with uid ${createRequest.reviewedUid} not found") }
        
        feedback.reviewer = reviewer
        feedback.reviewed = reviewed

        val savedFeedback = feedbackRepository.save(feedback)

        val feedbackDto = feedbackMapper.toDto(savedFeedback)

        feedbackEventProducer.produceRateSkillEvent(feedbackDto)

        return feedbackDto
    }

    fun update(id: Long, updateRequest: UpdateFeedbackRequest): FeedbackDto {
        val existingFeedback = feedbackRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Feedback with id $id not found") }
        
        val updatedFeedback = feedbackMapper.updateEntity(existingFeedback, updateRequest)

        val reviewer = userService.findEntityByUid(updateRequest.reviewerUid)
            .orElseThrow { IllegalArgumentException("Reviewer user with uid ${updateRequest.reviewerUid} not found") }
        val reviewed = userService.findEntityByUid(updateRequest.reviewedUid)
            .orElseThrow { IllegalArgumentException("Reviewed user with uid ${updateRequest.reviewedUid} not found") }
        
        updatedFeedback.reviewer = reviewer
        updatedFeedback.reviewed = reviewed
        
        val savedFeedback = feedbackRepository.save(updatedFeedback)
        return feedbackMapper.toDto(savedFeedback)
    }

    fun deleteById(id: Long) {
        if (!feedbackRepository.existsById(id)) {
            throw IllegalArgumentException("Feedback with id $id not found")
        }
        feedbackRepository.deleteById(id)
    }

    fun existsById(id: Long): Boolean = feedbackRepository.existsById(id)
}