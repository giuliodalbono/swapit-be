package io.github.giuliodalbono.swapit.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Status of a swap proposal", enumAsRef = true)
enum class SwapProposalStatus {
    @Schema(description = "Proposal has been created and is waiting for response")
    PENDING,

    @Schema(description = "Proposal has been accepted by the recipient")
    ACCEPTED,

    @Schema(description = "Proposal has been rejected by the recipient")
    REJECTED,

    @Schema(description = "Skill swap session has been completed")
    COMPLETED;
}