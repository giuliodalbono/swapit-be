package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import io.github.giuliodalbono.swapit.model.entity.SwapProposal
import org.springframework.data.jpa.repository.JpaRepository

interface SwapProposalRepository: JpaRepository<SwapProposal, Long> {
    fun findByRequestUserUid(requestUserUid: String): List<SwapProposal>
    fun findByOfferUserUid(offerUserUid: String): List<SwapProposal>
    fun findByStatus(status: SwapProposalStatus): List<SwapProposal>
}