package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.SkillDesired
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SkillDesiredRepository: JpaRepository<SkillDesired, Long> {
    fun findByUser(user: User): List<SkillDesired>
    fun findByUserUid(userUid: String): List<SkillDesired>
    fun findBySkill(skill: Skill): List<SkillDesired>
    fun findByUserAndSkill(user: User, skill: Skill): Optional<SkillDesired>
    fun existsByUserAndSkill(user: User, skill: Skill): Boolean
    fun deleteByUserAndSkill(user: User, skill: Skill)
}
