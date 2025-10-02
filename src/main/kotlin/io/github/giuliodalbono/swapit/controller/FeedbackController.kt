package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateFeedbackRequest
import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.dto.UpdateFeedbackRequest
import io.github.giuliodalbono.swapit.service.FeedbackService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feedbacks")
@Tag(name = "Feedback Management", description = "APIs for managing feedback and reviews")
class FeedbackController(private val feedbackService: FeedbackService) {

    @GetMapping
    @Operation(
        summary = "Get all feedbacks",
        description = "Retrieves a list of all feedback entries in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of feedbacks",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<FeedbackDto>::class)
                )]
            )
        ]
    )
    fun getAllFeedbacks(): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findAll()
        return ResponseEntity.ok(feedbacks)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get feedback by ID",
        description = "Retrieves a specific feedback by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Feedback found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Feedback not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getFeedbackById(
        @Parameter(
            description = "Unique identifier of the feedback to retrieve",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<FeedbackDto> {
        return feedbackService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/reviewer/{reviewerUid}")
    @Operation(
        summary = "Get feedbacks by reviewer",
        description = "Retrieves all feedbacks written by a specific user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved feedbacks by reviewer",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<FeedbackDto>::class)
                )]
            )
        ]
    )
    fun getFeedbacksByReviewer(
        @Parameter(
            description = "UID of the user who wrote the feedbacks",
            example = "user_12345",
            required = true
        ) @PathVariable reviewerUid: String
    ): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findByReviewer(reviewerUid)
        return ResponseEntity.ok(feedbacks)
    }

    @GetMapping("/reviewed/{reviewedUid}")
    @Operation(
        summary = "Get feedbacks by reviewed user",
        description = "Retrieves all feedbacks received by a specific user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved feedbacks by reviewed user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<FeedbackDto>::class)
                )]
            )
        ]
    )
    fun getFeedbacksByReviewed(
        @Parameter(
            description = "UID of the user who received the feedbacks",
            example = "user_67890",
            required = true
        ) @PathVariable reviewedUid: String
    ): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.findByReviewed(reviewedUid)
        return ResponseEntity.ok(feedbacks)
    }

    @PostMapping
    @Operation(
        summary = "Create new feedback",
        description = "Creates a new feedback entry in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Feedback created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createFeedback(
        @Parameter(
            description = "Feedback data for creation",
            required = true
        ) @RequestBody createRequest: CreateFeedbackRequest
    ): ResponseEntity<FeedbackDto> {
        val savedFeedback = feedbackService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFeedback)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update feedback",
        description = "Updates an existing feedback entry"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Feedback updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Feedback not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateFeedback(
        @Parameter(
            description = "Unique identifier of the feedback to update",
            example = "1",
            required = true
        ) @PathVariable id: Long,
        @Parameter(
            description = "Updated feedback data",
            required = true
        ) @RequestBody updateRequest: UpdateFeedbackRequest
    ): ResponseEntity<FeedbackDto> {
        return try {
            val updatedFeedback = feedbackService.update(id, updateRequest)
            ResponseEntity.ok(updatedFeedback)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete feedback",
        description = "Deletes a feedback entry from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Feedback deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Feedback not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteFeedback(
        @Parameter(
            description = "Unique identifier of the feedback to delete",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Void> {
        return try {
            feedbackService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(
        summary = "Check feedback existence",
        description = "Checks if a feedback exists by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Feedback Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkFeedbackExists(
        @Parameter(
            description = "Unique identifier of the feedback to check",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = feedbackService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}