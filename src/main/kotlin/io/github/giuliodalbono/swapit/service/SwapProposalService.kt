package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.mapper.SwapProposalMapper
import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import io.github.giuliodalbono.swapit.model.repository.SwapProposalRepository
import io.github.giuliodalbono.swapit.service.producer.SwapProposalEventProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class SwapProposalService(
    private val userService: UserService,
    private val skillService: SkillService,
    private val calendarService: CalendarService,
    private val swapProposalMapper: SwapProposalMapper,
    private val swapProposalRepository: SwapProposalRepository,
    private val swapProposalEventProducer: SwapProposalEventProducer
) {

    fun findAll(): List<SwapProposalDto> = swapProposalRepository.findAll().map { swapProposalMapper.toDto(it) }

    fun findById(id: Long): Optional<SwapProposalDto> = swapProposalRepository.findById(id).map { swapProposalMapper.toDto(it) }

    fun findByRequestUser(requestUserUid: String): List<SwapProposalDto> = 
        swapProposalRepository.findByRequestUserUid(requestUserUid).map { swapProposalMapper.toDto(it) }

    fun findByOfferUser(offerUserUid: String): List<SwapProposalDto> = 
        swapProposalRepository.findByOfferUserUid(offerUserUid).map { swapProposalMapper.toDto(it) }

    fun findByStatus(status: String): List<SwapProposalDto> =
        swapProposalRepository.findByStatus(SwapProposalStatus.valueOf(status)).map { swapProposalMapper.toDto(it) }

    fun save(createRequest: CreateSwapProposalRequest): SwapProposalDto {
        val swapProposal = swapProposalMapper.toEntity(createRequest)

        val requestUser = userService.findEntityByUid(createRequest.requestUserUid)
            .orElseThrow { IllegalArgumentException("Request user with uid ${createRequest.requestUserUid} not found") }
        val offerUser = userService.findEntityByUid(createRequest.offerUserUid)
            .orElseThrow { IllegalArgumentException("Offer user with uid ${createRequest.offerUserUid} not found") }
        val skillOffered = skillService.findEntityById(createRequest.skillOfferedId)
            .orElseThrow { IllegalArgumentException("Skill offered with id ${createRequest.skillOfferedId} not found") }
        val skillRequested = skillService.findEntityById(createRequest.skillRequestedId)
            .orElseThrow { IllegalArgumentException("Skill requested with id ${createRequest.skillRequestedId} not found") }
        
        swapProposal.requestUser = requestUser
        swapProposal.offerUser = offerUser
        swapProposal.skillOffered = skillOffered
        swapProposal.skillRequested = skillRequested
        
        val savedProposal = swapProposalRepository.save(swapProposal)
        return swapProposalMapper.toDto(savedProposal)
    }

    fun update(id: Long, updateRequest: UpdateSwapProposalRequest): SwapProposalDto {
        val existingProposal = swapProposalRepository.findById(id)
            .orElseThrow { IllegalArgumentException("SwapProposal with id $id not found") }
        
        val previousStatus = existingProposal.status!!
        val updatedProposal = swapProposalMapper.updateEntity(existingProposal, updateRequest)

        val requestUser = userService.findEntityByUid(updateRequest.requestUserUid)
            .orElseThrow { IllegalArgumentException("Request user with uid ${updateRequest.requestUserUid} not found") }
        val offerUser = userService.findEntityByUid(updateRequest.offerUserUid)
            .orElseThrow { IllegalArgumentException("Offer user with uid ${updateRequest.offerUserUid} not found") }
        val skillOffered = skillService.findEntityById(updateRequest.skillOfferedId)
            .orElseThrow { IllegalArgumentException("Skill offered with id ${updateRequest.skillOfferedId} not found") }
        val skillRequested = skillService.findEntityById(updateRequest.skillRequestedId)
            .orElseThrow { IllegalArgumentException("Skill requested with id ${updateRequest.skillRequestedId} not found") }

        updatedProposal.requestUser = requestUser
        updatedProposal.offerUser = offerUser
        updatedProposal.skillOffered = skillOffered
        updatedProposal.skillRequested = skillRequested
        
        val savedProposal = swapProposalRepository.save(updatedProposal)
        val savedProposalDto = swapProposalMapper.toDto(savedProposal)

        if (previousStatus != SwapProposalStatus.ACCEPTED && savedProposalDto.status == SwapProposalStatus.ACCEPTED) {
            // If status changes to ACCEPTED
            swapProposalEventProducer.produceSwappedEvent(savedProposalDto)

            // Commented out because if no credentials set in local, it will always throw exception
            // val gCalendarEventDto = swapProposalMapper.toGCalendarEventDto(savedProposal)
            // calendarService.createEventUsingOAuth2AfterCommit(gCalendarEventDto)
        }

        return savedProposalDto
    }

    fun deleteById(id: Long) {
        if (!swapProposalRepository.existsById(id)) {
            throw IllegalArgumentException("SwapProposal with id $id not found")
        }
        swapProposalRepository.deleteById(id)
    }

    fun existsById(id: Long): Boolean = swapProposalRepository.existsById(id)
}