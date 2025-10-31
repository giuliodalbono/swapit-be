package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.mapper.SwapProposalMapper
import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import io.github.giuliodalbono.swapit.model.entity.SwapProposal
import io.github.giuliodalbono.swapit.model.entity.Skill
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.repository.SwapProposalRepository
import io.github.giuliodalbono.swapit.service.producer.SwapProposalEventProducer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class SwapProposalServiceTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var skillService: SkillService

    @Mock
    private lateinit var calendarService: CalendarService

    @Mock
    private lateinit var swapProposalMapper: SwapProposalMapper

    @Mock
    private lateinit var swapProposalRepository: SwapProposalRepository

    @Mock
    private lateinit var swapProposalEventProducer: SwapProposalEventProducer

    @InjectMocks
    private lateinit var swapProposalService: SwapProposalService

    private val testId = 1L
    private val testDate = LocalDate.now()
    private val testStartTime = LocalTime.of(9, 0)
    private val testEndTime = LocalTime.of(11, 0)
    private val testPresentationLetter = "I'd love to learn cooking!"
    private val testStatus = SwapProposalStatus.PENDING
    private val testDateTime = LocalDateTime.now()

    private val testUser = User().apply {
        uid = "user123"
        email = "user@example.com"
        username = "testuser"
    }

    private val testSkill = Skill().apply {
        id = 1L
        label = "Cooking"
        description = "Cooking skills"
    }

    private val testSwapProposal = SwapProposal().apply {
        id = testId
        date = testDate
        startTime = testStartTime
        endTime = testEndTime
        presentationLetter = testPresentationLetter
        status = testStatus
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
        requestUser = testUser
        offerUser = testUser
        skillOffered = testSkill
        skillRequested = testSkill
    }

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
    fun `findAll should return all swap proposals`() {
        // Given
        val swapProposals = listOf(testSwapProposal)
        val swapProposalDtos = listOf(testSwapProposalDto)
        `when`(swapProposalRepository.findAll()).thenReturn(swapProposals)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.findAll()

        // Then
        assert(result == swapProposalDtos)
        verify(swapProposalRepository).findAll()
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `findById should return swap proposal when found`() {
        // Given
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(testSwapProposal))
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.findById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSwapProposalDto)
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `findByRequestUser should return swap proposals for request user`() {
        // Given
        val swapProposals = listOf(testSwapProposal)
        val swapProposalDtos = listOf(testSwapProposalDto)
        `when`(swapProposalRepository.findByRequestUserUid("user123")).thenReturn(swapProposals)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.findByRequestUser("user123")

        // Then
        assert(result == swapProposalDtos)
        verify(swapProposalRepository).findByRequestUserUid("user123")
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `findByOfferUser should return swap proposals for offer user`() {
        // Given
        val swapProposals = listOf(testSwapProposal)
        val swapProposalDtos = listOf(testSwapProposalDto)
        `when`(swapProposalRepository.findByOfferUserUid("user123")).thenReturn(swapProposals)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.findByOfferUser("user123")

        // Then
        assert(result == swapProposalDtos)
        verify(swapProposalRepository).findByOfferUserUid("user123")
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `findByStatus should return swap proposals with given status`() {
        // Given
        val swapProposals = listOf(testSwapProposal)
        val swapProposalDtos = listOf(testSwapProposalDto)
        `when`(swapProposalRepository.findByStatus(SwapProposalStatus.PENDING)).thenReturn(swapProposals)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.findByStatus("PENDING")

        // Then
        assert(result == swapProposalDtos)
        verify(swapProposalRepository).findByStatus(SwapProposalStatus.PENDING)
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `save should create and return swap proposal`() {
        // Given
        `when`(swapProposalMapper.toEntity(createSwapProposalRequest)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(testSwapProposal)).thenReturn(testSwapProposal)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(testSwapProposalDto)

        // When
        val result = swapProposalService.save(createSwapProposalRequest)

        // Then
        assert(result == testSwapProposalDto)
        verify(swapProposalMapper).toEntity(createSwapProposalRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(testSwapProposal)
        verify(swapProposalMapper).toDto(testSwapProposal)
    }

    @Test
    fun `save should throw exception when request user not found`() {
        // Given
        `when`(swapProposalMapper.toEntity(createSwapProposalRequest)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            swapProposalService.save(createSwapProposalRequest)
        }
        verify(swapProposalMapper).toEntity(createSwapProposalRequest)
        verify(userService).findEntityByUid("user123")
        verify(skillService, never()).findEntityById(1L)
        verify(swapProposalRepository, never()).save(testSwapProposal)
    }

    @Test
    fun `update should update and return swap proposal when found`() {
        // Given
        val updatedSwapProposalDto = testSwapProposalDto.copy(status = SwapProposalStatus.ACCEPTED)
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(testSwapProposal))
        `when`(swapProposalMapper.updateEntity(testSwapProposal, updateSwapProposalRequest)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(testSwapProposal)).thenReturn(testSwapProposal)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(updatedSwapProposalDto)

        // When
        val result = swapProposalService.update(testId, updateSwapProposalRequest)

        // Then
        assert(result == updatedSwapProposalDto)
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).updateEntity(testSwapProposal, updateSwapProposalRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(testSwapProposal)
        verify(swapProposalMapper).toDto(testSwapProposal)
        verify(swapProposalEventProducer).produceSwappedEvent(updatedSwapProposalDto)
    }

    @Test
    fun `update should not produce event when status does not change to ACCEPTED`() {
        // Given
        val updateRequestNoStatusChange = updateSwapProposalRequest.copy(status = SwapProposalStatus.REJECTED)
        val updatedSwapProposalDto = testSwapProposalDto.copy(status = SwapProposalStatus.REJECTED)
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(testSwapProposal))
        `when`(swapProposalMapper.updateEntity(testSwapProposal, updateRequestNoStatusChange)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(testSwapProposal)).thenReturn(testSwapProposal)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenReturn(updatedSwapProposalDto)

        // When
        val result = swapProposalService.update(testId, updateRequestNoStatusChange)

        // Then
        assert(result == updatedSwapProposalDto)
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).updateEntity(testSwapProposal, updateRequestNoStatusChange)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(testSwapProposal)
        verify(swapProposalMapper).toDto(testSwapProposal)
        verify(swapProposalEventProducer, never()).produceSwappedEvent(any())
    }

    @Test
    fun `update should not produce event when status was already ACCEPTED`() {
        // Given
        val alreadyAcceptedProposal = testSwapProposal.apply { status = SwapProposalStatus.ACCEPTED }
        val updateRequestAccepted = updateSwapProposalRequest.copy(status = SwapProposalStatus.ACCEPTED)
        val updatedSwapProposalDto = testSwapProposalDto.copy(status = SwapProposalStatus.ACCEPTED)
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(alreadyAcceptedProposal))
        `when`(swapProposalMapper.updateEntity(alreadyAcceptedProposal, updateRequestAccepted)).thenReturn(alreadyAcceptedProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(alreadyAcceptedProposal)).thenReturn(alreadyAcceptedProposal)
        `when`(swapProposalMapper.toDto(alreadyAcceptedProposal)).thenReturn(updatedSwapProposalDto)

        // When
        val result = swapProposalService.update(testId, updateRequestAccepted)

        // Then
        assert(result == updatedSwapProposalDto)
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).updateEntity(alreadyAcceptedProposal, updateRequestAccepted)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(alreadyAcceptedProposal)
        verify(swapProposalMapper).toDto(alreadyAcceptedProposal)
        verify(swapProposalEventProducer, never()).produceSwappedEvent(any())
    }

    @Test
    fun `update should not produce event when DTO mapping fails`() {
        // Given
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(testSwapProposal))
        `when`(swapProposalMapper.updateEntity(testSwapProposal, updateSwapProposalRequest)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(testSwapProposal)).thenReturn(testSwapProposal)
        `when`(swapProposalMapper.toDto(testSwapProposal)).thenThrow(RuntimeException("Mapping error"))

        // When & Then
        assertThrows<RuntimeException> {
            swapProposalService.update(testId, updateSwapProposalRequest)
        }
        
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).updateEntity(testSwapProposal, updateSwapProposalRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(testSwapProposal)
        verify(swapProposalMapper).toDto(testSwapProposal)
        verify(swapProposalEventProducer, never()).produceSwappedEvent(any())
    }

    @Test
    fun `update should not produce event when database save fails`() {
        // Given
        `when`(swapProposalRepository.findById(testId)).thenReturn(Optional.of(testSwapProposal))
        `when`(swapProposalMapper.updateEntity(testSwapProposal, updateSwapProposalRequest)).thenReturn(testSwapProposal)
        `when`(userService.findEntityByUid("user123")).thenReturn(Optional.of(testUser))
        `when`(skillService.findEntityById(1L)).thenReturn(Optional.of(testSkill))
        `when`(swapProposalRepository.save(testSwapProposal)).thenThrow(RuntimeException("Database error"))

        // When & Then
        assertThrows<RuntimeException> {
            swapProposalService.update(testId, updateSwapProposalRequest)
        }
        
        verify(swapProposalRepository).findById(testId)
        verify(swapProposalMapper).updateEntity(testSwapProposal, updateSwapProposalRequest)
        verify(userService, times(2)).findEntityByUid("user123")
        verify(skillService, times(2)).findEntityById(1L)
        verify(swapProposalRepository).save(testSwapProposal)
        verify(swapProposalMapper, never()).toDto(testSwapProposal)
        verify(swapProposalEventProducer, never()).produceSwappedEvent(any())
    }

    @Test
    fun `deleteById should delete swap proposal when exists`() {
        // Given
        `when`(swapProposalRepository.existsById(testId)).thenReturn(true)

        // When
        swapProposalService.deleteById(testId)

        // Then
        verify(swapProposalRepository).existsById(testId)
        verify(swapProposalRepository).deleteById(testId)
    }

    @Test
    fun `deleteById should throw exception when swap proposal not found`() {
        // Given
        `when`(swapProposalRepository.existsById(testId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            swapProposalService.deleteById(testId)
        }
        verify(swapProposalRepository).existsById(testId)
        verify(swapProposalRepository, never()).deleteById(any())
    }
}
