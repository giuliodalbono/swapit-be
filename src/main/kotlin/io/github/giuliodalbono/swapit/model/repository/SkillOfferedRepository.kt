package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.SkillOffered
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SkillOfferedRepository: JpaRepository<SkillOffered, Long> {
    fun findByUser(user: User): List<SkillOffered>
    fun findByUserUid(userUid: String): List<SkillOffered>
    fun findBySkill(skill: Skill): List<SkillOffered>
    fun findByUserAndSkill(user: User, skill: Skill): Optional<SkillOffered>
    fun existsByUserAndSkill(user: User, skill: Skill): Boolean
    fun deleteByUserAndSkill(user: User, skill: Skill)
}
