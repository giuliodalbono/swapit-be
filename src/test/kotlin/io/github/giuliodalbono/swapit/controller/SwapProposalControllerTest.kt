package io.github.giuliodalbono.swapit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import io.github.giuliodalbono.swapit.service.SwapProposalService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@WebMvcTest(SwapProposalController::class)
class SwapProposalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var swapProposalService: SwapProposalService

    private val testId = 1L
    private val testDate = LocalDate.now()
    private val testStartTime = LocalTime.of(9, 0)
    private val testEndTime = LocalTime.of(11, 0)
    private val testPresentationLetter = "I'd love to learn cooking!"
    private val testStatus = SwapProposalStatus.PENDING
    private val testDateTime = LocalDateTime.now()

    private val testSwapProposalDto = SwapProposalDto(
        id = testId,
        date = testDate,
        startTime = testStartTime,
        endTime = testEndTime,
        presentationLetter = testPresentationLetter,
        status = testStatus,
        version = 0L,
        creationTime = testDateTime,
        lastUpdate = testDateTime,
        skillOfferedId = 1L,
        skillRequestedId = 1L,
        requestUserUid = "user123",
        offerUserUid = "user123"
    )

    private val createSwapProposalRequest = CreateSwapProposalRequest(
        date = testDate,
        startTime = testStartTime,
        endTime = testEndTime,
        presentationLetter = testPresentationLetter,
        status = testStatus,
        skillOfferedId = 1L,
        skillRequestedId = 1L,
        requestUserUid = "user123",
        offerUserUid = "user123"
    )

    private val updateSwapProposalRequest = UpdateSwapProposalRequest(
        date = testDate.plusDays(1),
        startTime = testStartTime,
        endTime = testEndTime,
        presentationLetter = "Updated presentation",
        status = SwapProposalStatus.ACCEPTED,
        skillOfferedId = 1L,
        skillRequestedId = 1L,
        requestUserUid = "user123",
        offerUserUid = "user123"
    )

    @Test
    fun `getAllSwapProposals should return all swap proposals`() {
        // Given
        val swapProposals = listOf(testSwapProposalDto)
        `when`(swapProposalService.findAll()).thenReturn(swapProposals)

        // When & Then
        mockMvc.perform(get("/api/swap-proposals"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[0].skillOfferedId").value(1L))
            .andExpect(jsonPath("$[0].skillRequestedId").value(1L))
    }

    @Test
    fun `getSwapProposalById should return swap proposal when found`() {
        // Given
        `when`(swapProposalService.findById(testId)).thenReturn(Optional.of(testSwapProposalDto))

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/{id}", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.skillOfferedId").value(1L))
            .andExpect(jsonPath("$.skillRequestedId").value(1L))
    }

    @Test
    fun `getSwapProposalById should return 404 when not found`() {
        // Given
        `when`(swapProposalService.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getSwapProposalsByRequestUser should return swap proposals for request user`() {
        // Given
        val swapProposals = listOf(testSwapProposalDto)
        `when`(swapProposalService.findByRequestUser("user123")).thenReturn(swapProposals)

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/request-user/{requestUserUid}", "user123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].requestUserUid").value("user123"))
    }

    @Test
    fun `getSwapProposalsByOfferUser should return swap proposals for offer user`() {
        // Given
        val swapProposals = listOf(testSwapProposalDto)
        `when`(swapProposalService.findByOfferUser("user123")).thenReturn(swapProposals)

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/offer-user/{offerUserUid}", "user123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].offerUserUid").value("user123"))
    }

    @Test
    fun `getSwapProposalsByStatus should return swap proposals with given status`() {
        // Given
        val swapProposals = listOf(testSwapProposalDto)
        `when`(swapProposalService.findByStatus("PENDING")).thenReturn(swapProposals)

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/status/{status}", "PENDING"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
    }

    @Test
    fun `createSwapProposal should create and return swap proposal`() {
        // Given
        `when`(swapProposalService.save(createSwapProposalRequest)).thenReturn(testSwapProposalDto)

        // When & Then
        mockMvc.perform(post("/api/swap-proposals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSwapProposalRequest)))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.skillOfferedId").value(1L))
            .andExpect(jsonPath("$.skillRequestedId").value(1L))
    }

    @Test
    fun `updateSwapProposal should update and return swap proposal when found`() {
        // Given
        val updatedSwapProposalDto = testSwapProposalDto.copy(status = SwapProposalStatus.ACCEPTED)
        `when`(swapProposalService.update(testId, updateSwapProposalRequest)).thenReturn(updatedSwapProposalDto)

        // When & Then
        mockMvc.perform(put("/api/swap-proposals/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSwapProposalRequest)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.status").value("ACCEPTED"))
    }

    @Test
    fun `updateSwapProposal should return 404 when swap proposal not found`() {
        // Given
        `when`(swapProposalService.update(testId, updateSwapProposalRequest)).thenThrow(IllegalArgumentException("SwapProposal not found"))

        // When & Then
        mockMvc.perform(put("/api/swap-proposals/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSwapProposalRequest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteSwapProposal should delete swap proposal when exists`() {
        // Given
        doNothing().`when`(swapProposalService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/swap-proposals/{id}", testId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteSwapProposal should return 404 when swap proposal not found`() {
        // Given
        doThrow(IllegalArgumentException("SwapProposal not found")).`when`(swapProposalService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/swap-proposals/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `checkSwapProposalExists should return true when swap proposal exists`() {
        // Given
        `when`(swapProposalService.existsById(testId)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/swap-proposals/{id}/exists", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }
}
