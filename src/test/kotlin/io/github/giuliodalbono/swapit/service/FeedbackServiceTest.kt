package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.mapper.FeedbackMapper
import io.github.giuliodalbono.swapit.model.entity.Feedback
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.repository.FeedbackRepository
import io.github.giuliodalbono.swapit.service.producer.FeedbackEventProducer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class FeedbackServiceTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var feedbackMapper: FeedbackMapper

    @Mock
    private lateinit var feedbackRepository: FeedbackRepository

    @Mock
    private lateinit var feedbackEventProducer: FeedbackEventProducer

    @InjectMocks
    private lateinit var feedbackService: FeedbackService

    private val testId = 1L
    private val testRating = 5L
    private val testReview = "Great teaching session!"
    private val testDateTime = LocalDateTime.now()

    private val testUser = User().apply {
        uid = "user123"
        email = "user@example.com"
        username = "testuser"
    }

    private val testFeedback = Feedback().apply {
        id = testId
        rating = testRating
        review = testReview
        version = 0L
        reviewer = testUser
        reviewed = testUser
    }

    private val testFeedbackDto = FeedbackDto(
        id = testId,
        rating = testRating,
        review = testReview,
        version = 0L,
        creationTime = testDateTime,
        lastUpdate = testDateTime,
        reviewerUid = "user123",
        reviewedUid = "user123"
    )

    private val createFeedbackRequest = CreateFeedbackRequest(
        rating = testRating,
        review = testReview,
        reviewerUid = "user123",
        reviewedUid = "user123"
    )

    private val updateFeedbackRequest = UpdateFeedbackRequest(
        rating = 4L,
        review = "Updated review",
        reviewerUid = "user123",
        reviewedUid = "user123"
    )

    @Test
    fun `findAll should return all feedbacks`() {
        // Given
        val feedbacks = listOf(testFeedback)
        val feedbackDtos = listOf(testFeedbackDto)
        `when`(feedbackRepository.findAll()).thenReturn(feedbacks)
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDto)

        // When
        val result = feedbackService.findAll()

        // Then
        assert(result == feedbackDtos)
        verify(feedbackRepository).findAll()
        verify(feedbackMapper).toDto(testFeedback)
    }

    @Test
    fun `findById should return feedback when found`() {
        // Given
        `when`(feedbackRepository.findById(testId)).thenReturn(Optional.of(testFeedback))
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDto)

        // When
        val result = feedbackService.findById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testFeedbackDto)
        verify(feedbackRepository).findById(testId)
        verify(feedbackMapper).toDto(testFeedback)
    }

    @Test
    fun `findById should return empty when not found`() {
        // Given
        `when`(feedbackRepository.findById(testId)).thenReturn(Optional.empty())

        // When
        val result = feedbackService.findById(testId)

        // Then
        assert(!result.isPresent)
        verify(feedbackRepository).findById(testId)
        verify(feedbackMapper, never()).toDto(testFeedback)
    }

    @Test
    fun `findByReviewer should return feedbacks for reviewer`() {
        // Given
        val feedbacks = listOf(testFeedback)
        val feedbackDtos = listOf(testFeedbackDto)
        `when`(feedbackRepository.findByReviewerUid("user123")).thenReturn(feedbacks)
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDto)

        // When
        val result = feedbackService.findByReviewer("user123")

        // Then
        assert(result == feedbackDtos)
        verify(feedbackRepository).findByReviewerUid("user123")
        verify(feedbackMapper).toDto(testFeedback)
    }

    @Test
    fun `findByReviewed should return feedbacks for reviewed user`() {
        // Given
        val feedbacks = listOf(testFeedback)
        val feedbackDtos = listOf(testFeedbackDto)
        `when`(feedbackRepository.findByReviewedUid("user123")).thenReturn(feedbacks)
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDto)

        // When
        val result = feedbackService.findByReviewed("user123")

        // Then
        assert(result == feedbackDtos)
        verify(feedbackRepository).findByReviewedUid("user123")
        verify(feedbackMapper).toDto(testFeedback)
    }

    @Test
    fun `save should create and return feedback`() {
        // Given
        `when`(feedbackMapper.toEntity(createFeedbackRequest)).thenReturn(testFeedback)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(feedbackRepository.save(testFeedback)).thenReturn(testFeedback)
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDto)

        // When
        val result = feedbackService.save(createFeedbackRequest)

        // Then
        assert(result == testFeedbackDto)
        verify(feedbackMapper).toEntity(createFeedbackRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(feedbackRepository).save(testFeedback)
        verify(feedbackMapper).toDto(testFeedback)
        verify(feedbackEventProducer).produceRateSkillEvent(testFeedbackDto)
    }

    @Test
    fun `save should throw exception when reviewer not found`() {
        // Given
        `when`(feedbackMapper.toEntity(createFeedbackRequest)).thenReturn(testFeedback)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            feedbackService.save(createFeedbackRequest)
        }
        verify(feedbackMapper).toEntity(createFeedbackRequest)
        verify(userService).findEntityByUid("user123")
        verify(feedbackRepository, never()).save(any())
        verify(feedbackEventProducer, never()).produceRateSkillEvent(any())
    }

    @Test
    fun `save should not produce event when database save fails`() {
        // Given
        `when`(feedbackMapper.toEntity(createFeedbackRequest)).thenReturn(testFeedback)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(feedbackRepository.save(testFeedback)).thenThrow(RuntimeException("Database error"))

        // When & Then
        assertThrows<RuntimeException> {
            feedbackService.save(createFeedbackRequest)
        }
        
        verify(feedbackMapper).toEntity(createFeedbackRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(feedbackRepository).save(testFeedback)
        verify(feedbackMapper, never()).toDto(testFeedback)
        verify(feedbackEventProducer, never()).produceRateSkillEvent(any())
    }

    @Test
    fun `save should not produce event when DTO mapping fails`() {
        // Given
        `when`(feedbackMapper.toEntity(createFeedbackRequest)).thenReturn(testFeedback)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(feedbackRepository.save(testFeedback)).thenReturn(testFeedback)
        `when`(feedbackMapper.toDto(testFeedback)).thenThrow(RuntimeException("Mapping error"))

        // When & Then
        assertThrows<RuntimeException> {
            feedbackService.save(createFeedbackRequest)
        }
        
        verify(feedbackMapper).toEntity(createFeedbackRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(feedbackRepository).save(testFeedback)
        verify(feedbackMapper).toDto(testFeedback)
        verify(feedbackEventProducer, never()).produceRateSkillEvent(any())
    }

    @Test
    fun `update should update and return feedback when found`() {
        // Given
        val updatedFeedbackDto = testFeedbackDto.copy(rating = 4L, review = "Updated review")
        `when`(feedbackRepository.findById(testId)).thenReturn(Optional.of(testFeedback))
        `when`(feedbackMapper.updateEntity(testFeedback, updateFeedbackRequest)).thenReturn(testFeedback)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(feedbackRepository.save(testFeedback)).thenReturn(testFeedback)
        `when`(feedbackMapper.toDto(testFeedback)).thenReturn(updatedFeedbackDto)

        // When
        val result = feedbackService.update(testId, updateFeedbackRequest)

        // Then
        assert(result == updatedFeedbackDto)
        verify(feedbackRepository).findById(testId)
        verify(feedbackMapper).updateEntity(testFeedback, updateFeedbackRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(feedbackRepository).save(testFeedback)
        verify(feedbackMapper).toDto(testFeedback)
        verify(feedbackEventProducer, never()).produceRateSkillEvent(any())
    }

    @Test
    fun `update should throw exception when feedback not found`() {
        // Given
        `when`(feedbackRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            feedbackService.update(testId, updateFeedbackRequest)
        }
        verify(feedbackRepository).findById(testId)
        verify(feedbackMapper, never()).updateEntity(testFeedback, updateFeedbackRequest)
        verify(feedbackRepository, never()).save(testFeedback)
        verify(feedbackEventProducer, never()).produceRateSkillEvent(any())
    }

    @Test
    fun `deleteById should delete feedback when exists`() {
        // Given
        `when`(feedbackRepository.existsById(testId)).thenReturn(true)

        // When
        feedbackService.deleteById(testId)

        // Then
        verify(feedbackRepository).existsById(testId)
        verify(feedbackRepository).deleteById(testId)
    }

    @Test
    fun `deleteById should throw exception when feedback not found`() {
        // Given
        `when`(feedbackRepository.existsById(testId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            feedbackService.deleteById(testId)
        }
        verify(feedbackRepository).existsById(testId)
        verify(feedbackRepository, never()).deleteById(any())
    }

    @Test
    fun `existsById should return true when feedback exists`() {
        // Given
        `when`(feedbackRepository.existsById(testId)).thenReturn(true)

        // When
        val result = feedbackService.existsById(testId)

        // Then
        assert(result)
        verify(feedbackRepository).existsById(testId)
    }
}
