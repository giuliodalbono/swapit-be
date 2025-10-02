package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.service.SwapProposalService
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
@RequestMapping("/api/swap-proposals")
@Tag(name = "Swap Proposal Management", description = "APIs for managing skill swap proposals")
class SwapProposalController(private val swapProposalService: SwapProposalService) {

    @GetMapping
    @Operation(
        summary = "Get all swap proposals",
        description = "Retrieves a list of all swap proposals in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of swap proposals",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SwapProposalDto>::class)
                )]
            )
        ]
    )
    fun getAllSwapProposals(): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findAll()
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get swap proposal by ID",
        description = "Retrieves a specific swap proposal by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Swap proposal found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SwapProposalDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Swap proposal not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getSwapProposalById(
        @Parameter(
            description = "Unique identifier of the swap proposal to retrieve",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<SwapProposalDto> {
        return swapProposalService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/request-user/{requestUserUid}")
    @Operation(
        summary = "Get swap proposals by request user",
        description = "Retrieves all swap proposals initiated by a specific user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved swap proposals by request user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SwapProposalDto>::class)
                )]
            )
        ]
    )
    fun getSwapProposalsByRequestUser(
        @Parameter(
            description = "UID of the user who initiated the proposals",
            example = "user_12345",
            required = true
        ) @PathVariable requestUserUid: String
    ): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByRequestUser(requestUserUid)
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/offer-user/{offerUserUid}")
    @Operation(
        summary = "Get swap proposals by offer user",
        description = "Retrieves all swap proposals where a specific user is being offered to"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved swap proposals by offer user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SwapProposalDto>::class)
                )]
            )
        ]
    )
    fun getSwapProposalsByOfferUser(
        @Parameter(
            description = "UID of the user who is being offered to",
            example = "user_67890",
            required = true
        ) @PathVariable offerUserUid: String
    ): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByOfferUser(offerUserUid)
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get swap proposals by status",
        description = "Retrieves all swap proposals with a specific status"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved swap proposals by status",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SwapProposalDto>::class)
                )]
            )
        ]
    )
    fun getSwapProposalsByStatus(
        @Parameter(
            description = "Status of the swap proposals to retrieve",
            example = "PENDING",
            required = true
        ) @PathVariable status: String
    ): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByStatus(status)
        return ResponseEntity.ok(proposals)
    }

    @PostMapping
    @Operation(
        summary = "Create new swap proposal",
        description = "Creates a new swap proposal in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Swap proposal created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SwapProposalDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createSwapProposal(
        @Parameter(
            description = "Swap proposal data for creation",
            required = true
        ) @RequestBody createRequest: CreateSwapProposalRequest
    ): ResponseEntity<SwapProposalDto> {
        val savedProposal = swapProposalService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProposal)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update swap proposal",
        description = "Updates an existing swap proposal"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Swap proposal updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SwapProposalDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Swap proposal not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateSwapProposal(
        @Parameter(
            description = "Unique identifier of the swap proposal to update",
            example = "1",
            required = true
        ) @PathVariable id: Long,
        @Parameter(
            description = "Updated swap proposal data",
            required = true
        ) @RequestBody updateRequest: UpdateSwapProposalRequest
    ): ResponseEntity<SwapProposalDto> {
        return try {
            val updatedProposal = swapProposalService.update(id, updateRequest)
            ResponseEntity.ok(updatedProposal)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete swap proposal",
        description = "Deletes a swap proposal from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Swap proposal deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Swap proposal not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSwapProposal(
        @Parameter(
            description = "Unique identifier of the swap proposal to delete",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Void> {
        return try {
            swapProposalService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(
        summary = "Check swap proposal existence",
        description = "Checks if a swap proposal exists by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Swap Proposal Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkSwapProposalExists(
        @Parameter(
            description = "Unique identifier of the swap proposal to check",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = swapProposalService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}