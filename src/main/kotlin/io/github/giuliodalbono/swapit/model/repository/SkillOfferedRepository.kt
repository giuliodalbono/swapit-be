package io.github.giuliodalbono.swapit.model.repository

import io.github.giuliodalbono.swapit.model.entity.SkillOffered
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SkillOfferedRepository: JpaRepository<SkillOffered, Long> {
    fun findByUser(user: User): List<SkillOffered>
    fun findByUserUid(userUid: String): List<SkillOffered>
    fun findBySkill(skill: Skill): List<SkillOffered>
    fun findByUserAndSkill(user: User, skill: Skill): Optional<SkillOffered>
    fun existsByUserAndSkill(user: User, skill: Skill): Boolean
    @Modifying
    @Query("DELETE FROM SkillOffered so WHERE so.user.uid = :userUid AND so.skill.id = :skillId")
    fun deleteByUserUidAndSkillId(userUid: String, skillId: Long): Int
}
