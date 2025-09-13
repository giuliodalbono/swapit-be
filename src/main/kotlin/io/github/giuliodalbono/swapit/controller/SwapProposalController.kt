package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.service.SwapProposalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/swap-proposals")
class SwapProposalController(private val swapProposalService: SwapProposalService) {

    @GetMapping
    fun getAllSwapProposals(): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findAll()
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/{id}")
    fun getSwapProposalById(@PathVariable id: Long): ResponseEntity<SwapProposalDto> {
        return swapProposalService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/request-user/{requestUserUid}")
    fun getSwapProposalsByRequestUser(@PathVariable requestUserUid: String): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByRequestUser(requestUserUid)
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/offer-user/{offerUserUid}")
    fun getSwapProposalsByOfferUser(@PathVariable offerUserUid: String): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByOfferUser(offerUserUid)
        return ResponseEntity.ok(proposals)
    }

    @GetMapping("/status/{status}")
    fun getSwapProposalsByStatus(@PathVariable status: String): ResponseEntity<List<SwapProposalDto>> {
        val proposals = swapProposalService.findByStatus(status)
        return ResponseEntity.ok(proposals)
    }

    @PostMapping
    fun createSwapProposal(@RequestBody createRequest: CreateSwapProposalRequest): ResponseEntity<SwapProposalDto> {
        val savedProposal = swapProposalService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProposal)
    }

    @PutMapping("/{id}")
    fun updateSwapProposal(@PathVariable id: Long, @RequestBody updateRequest: UpdateSwapProposalRequest): ResponseEntity<SwapProposalDto> {
        return try {
            val updatedProposal = swapProposalService.update(id, updateRequest)
            ResponseEntity.ok(updatedProposal)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSwapProposal(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            swapProposalService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    fun checkSwapProposalExists(@PathVariable id: Long): ResponseEntity<Map<String, Boolean>> {
        val exists = swapProposalService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}