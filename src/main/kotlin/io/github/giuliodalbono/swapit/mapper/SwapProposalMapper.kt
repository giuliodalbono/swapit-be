package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.model.entity.SwapProposal
import org.springframework.stereotype.Component

@Component
class SwapProposalMapper {

    fun toDto(swapProposal: SwapProposal): SwapProposalDto {
        return SwapProposalDto(
            id = swapProposal.id!!,
            date = swapProposal.date!!,
            startTime = swapProposal.startTime!!,
            endTime = swapProposal.endTime!!,
            presentationLetter = swapProposal.presentationLetter,
            status = swapProposal.status!!,
            version = swapProposal.version,
            creationTime = swapProposal.creationTime!!,
            lastUpdate = swapProposal.lastUpdate!!,
            skillOfferedId = swapProposal.skillOffered!!.id!!,
            skillRequestedId = swapProposal.skillRequested!!.id!!,
            requestUserUid = swapProposal.requestUser!!.uid!!,
            offerUserUid = swapProposal.offerUser!!.uid!!
        )
    }

    fun toEntity(createRequest: CreateSwapProposalRequest): SwapProposal {
        return SwapProposal().apply {
            date = createRequest.date
            startTime = createRequest.startTime
            endTime = createRequest.endTime
            presentationLetter = createRequest.presentationLetter
            status = createRequest.status
        }
    }

    fun updateEntity(swapProposal: SwapProposal, updateRequest: UpdateSwapProposalRequest): SwapProposal {
        swapProposal.date = updateRequest.date
        swapProposal.startTime = updateRequest.startTime
        swapProposal.endTime = updateRequest.endTime
        swapProposal.presentationLetter = updateRequest.presentationLetter
        swapProposal.status = updateRequest.status
        return swapProposal
    }
}
