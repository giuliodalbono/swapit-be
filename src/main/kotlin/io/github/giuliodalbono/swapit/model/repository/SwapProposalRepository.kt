package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.SwapProposal
import org.springframework.data.jpa.repository.JpaRepository

interface SwapProposalRepository: JpaRepository<SwapProposal, Long>