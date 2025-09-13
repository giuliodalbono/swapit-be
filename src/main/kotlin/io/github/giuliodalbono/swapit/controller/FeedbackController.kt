package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.service.FeedbackService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(private val feedbackService: FeedbackService) {

    @GetMapping
    fun getAllFeedbacks(): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findAll()
        return ResponseEntity.ok(feedbacks)
    }

    @GetMapping("/{id}")
    fun getFeedbackById(@PathVariable id: Long): ResponseEntity<FeedbackDto> {
        return feedbackService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/reviewer/{reviewerUid}")
    fun getFeedbacksByReviewer(@PathVariable reviewerUid: String): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findByReviewer(reviewerUid)
        return ResponseEntity.ok(feedbacks)
    }

    @GetMapping("/reviewed/{reviewedUid}")
    fun getFeedbacksByReviewed(@PathVariable reviewedUid: String): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findByReviewed(reviewedUid)
        return ResponseEntity.ok(feedbacks)
    }

    @PostMapping
    fun createFeedback(@RequestBody createRequest: CreateFeedbackRequest): ResponseEntity<FeedbackDto> {
        val savedFeedback = feedbackService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFeedback)
    }

    @PutMapping("/{id}")
    fun updateFeedback(@PathVariable id: Long, @RequestBody updateRequest: UpdateFeedbackRequest): ResponseEntity<FeedbackDto> {
        return try {
            val updatedFeedback = feedbackService.update(id, updateRequest)
            ResponseEntity.ok(updatedFeedback)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteFeedback(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            feedbackService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    fun checkFeedbackExists(@PathVariable id: Long): ResponseEntity<Map<String, Boolean>> {
        val exists = feedbackService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}