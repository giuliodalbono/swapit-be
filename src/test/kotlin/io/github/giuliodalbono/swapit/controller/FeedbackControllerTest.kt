package io.github.giuliodalbono.swapit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.service.FeedbackService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(FeedbackController::class)
class FeedbackControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var feedbackService: FeedbackService

    private val testId = 1L
    private val testRating = 5L
    private val testReview = "Great teaching session!"
    private val testDateTime = LocalDateTime.now()

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
    fun `getAllFeedbacks should return all feedbacks`() {
        // Given
        val feedbacks = listOf(testFeedbackDto)
        `when`(feedbackService.findAll()).thenReturn(feedbacks)

        // When & Then
        mockMvc.perform(get("/api/feedbacks"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].rating").value(testRating))
            .andExpect(jsonPath("$[0].review").value(testReview))
            .andExpect(jsonPath("$[0].reviewerUid").value("user123"))
            .andExpect(jsonPath("$[0].reviewedUid").value("user123"))
    }

    @Test
    fun `getFeedbackById should return feedback when found`() {
        // Given
        `when`(feedbackService.findById(testId)).thenReturn(Optional.of(testFeedbackDto))

        // When & Then
        mockMvc.perform(get("/api/feedbacks/{id}", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.rating").value(testRating))
            .andExpect(jsonPath("$.review").value(testReview))
            .andExpect(jsonPath("$.reviewerUid").value("user123"))
            .andExpect(jsonPath("$.reviewedUid").value("user123"))
    }

    @Test
    fun `getFeedbackById should return 404 when not found`() {
        // Given
        `when`(feedbackService.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/feedbacks/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getFeedbacksByReviewer should return feedbacks for reviewer`() {
        // Given
        val feedbacks = listOf(testFeedbackDto)
        `when`(feedbackService.findByReviewer("user123")).thenReturn(feedbacks)

        // When & Then
        mockMvc.perform(get("/api/feedbacks/reviewer/{reviewerUid}", "user123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].reviewerUid").value("user123"))
    }

    @Test
    fun `getFeedbacksByReviewed should return feedbacks for reviewed user`() {
        // Given
        val feedbacks = listOf(testFeedbackDto)
        `when`(feedbackService.findByReviewed("user123")).thenReturn(feedbacks)

        // When & Then
        mockMvc.perform(get("/api/feedbacks/reviewed/{reviewedUid}", "user123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].reviewedUid").value("user123"))
    }

    @Test
    fun `createFeedback should create and return feedback`() {
        // Given
        `when`(feedbackService.save(createFeedbackRequest)).thenReturn(testFeedbackDto)

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createFeedbackRequest)))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.rating").value(testRating))
            .andExpect(jsonPath("$.review").value(testReview))
            .andExpect(jsonPath("$.reviewerUid").value("user123"))
            .andExpect(jsonPath("$.reviewedUid").value("user123"))
    }

    @Test
    fun `updateFeedback should update and return feedback when found`() {
        // Given
        val updatedFeedbackDto = testFeedbackDto.copy(rating = 4L, review = "Updated review")
        `when`(feedbackService.update(testId, updateFeedbackRequest)).thenReturn(updatedFeedbackDto)

        // When & Then
        mockMvc.perform(put("/api/feedbacks/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateFeedbackRequest)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.rating").value(4L))
            .andExpect(jsonPath("$.review").value("Updated review"))
    }

    @Test
    fun `updateFeedback should return 404 when feedback not found`() {
        // Given
        `when`(feedbackService.update(testId, updateFeedbackRequest)).thenThrow(IllegalArgumentException("Feedback not found"))

        // When & Then
        mockMvc.perform(put("/api/feedbacks/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateFeedbackRequest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteFeedback should delete feedback when exists`() {
        // Given
        doNothing().`when`(feedbackService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/feedbacks/{id}", testId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteFeedback should return 404 when feedback not found`() {
        // Given
        doThrow(IllegalArgumentException("Feedback not found")).`when`(feedbackService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/feedbacks/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `checkFeedbackExists should return true when feedback exists`() {
        // Given
        `when`(feedbackService.existsById(testId)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/feedbacks/{id}/exists", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }
}
