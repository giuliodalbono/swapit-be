package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateSwapProposalRequest
import io.github.giuliodalbono.swapit.dto.GCalendarEventDto
import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.dto.UpdateSwapProposalRequest
import io.github.giuliodalbono.swapit.model.entity.SwapProposal
import org.springframework.stereotype.Component
import java.time.ZoneId

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
            creationTime = swapProposal.creationTime,
            lastUpdate = swapProposal.lastUpdate,
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

    fun toGCalendarEventDto(swapProposal: SwapProposal): GCalendarEventDto {
        return GCalendarEventDto(
            summary = "Swap skill session",
            description = """
                User offering: ${swapProposal.offerUser!!.username},
                User requesting: ${swapProposal.requestUser!!.username},
                Skill offered: ${swapProposal.skillOffered!!.label},
                Skill requested: ${swapProposal.skillRequested!!.label}
            """.trimIndent(),
            startDateTime = swapProposal.date!!.atTime(swapProposal.startTime).atZone(ZoneId.systemDefault()),
            endDateTime = swapProposal.date!!.atTime(swapProposal.endTime).atZone(ZoneId.systemDefault()),
            timeZone = ZoneId.systemDefault().id,
            attendees = listOf(
                swapProposal.offerUser!!.email!!,
                swapProposal.requestUser!!.email!!,
            )
        )
    }
}
