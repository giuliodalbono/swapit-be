package io.github.giuliodalbono.swapit.service.producer

import io.github.giuliodalbono.swapit.dto.SwapProposalDto
import io.github.giuliodalbono.swapit.service.CloudEventContext
import io.github.giuliodalbono.swapit.service.CloudEventService
import org.springframework.stereotype.Service

@Service
class SwapProposalEventProducer(
    private val cloudEventService: CloudEventService
) {

    companion object {
        const val SWAPPED_TYPE = "Swapped"
        const val SKILL_SUBJECT = "Skill"
        const val SWAP_PROPOSAL_TOPIC = "SwapProposalEvent"
    }

    fun produceSwappedEvent(swapProposal: SwapProposalDto) {
        val cloudEventContext = CloudEventContext(subject = SKILL_SUBJECT, type = SWAPPED_TYPE, data = swapProposal)
        cloudEventService.sendCloudEventAfterCommit(cloudEventContext, SWAP_PROPOSAL_TOPIC)
    }
}
