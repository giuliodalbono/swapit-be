package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.SkillDesired
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SkillDesiredRepository: JpaRepository<SkillDesired, Long> {
    fun findByUser(user: User): List<SkillDesired>
    fun findByUserUid(userUid: String): List<SkillDesired>
    fun findBySkill(skill: Skill): List<SkillDesired>
    fun findByUserAndSkill(user: User, skill: Skill): Optional<SkillDesired>
    fun existsByUserAndSkill(user: User, skill: Skill): Boolean
    @Modifying
    @Query("DELETE FROM SkillDesired so WHERE so.user.uid = :userUid AND so.skill.id = :skillId")
    fun deleteByUserUidAndSkillId(userUid: String, skillId: Long): Int
}
