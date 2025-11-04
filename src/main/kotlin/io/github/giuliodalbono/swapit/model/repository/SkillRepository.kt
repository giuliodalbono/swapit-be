package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SkillRepository: JpaRepository<Skill, Long> {
    fun findByLabel(label: String): Optional<Skill>
    fun existsByLabel(label: String): Boolean
    fun findAllByLabelContaining(label: String): Set<Skill>
}