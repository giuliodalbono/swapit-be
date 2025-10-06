package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateSkillOfferedRequest
import io.github.giuliodalbono.swapit.dto.SkillOfferedDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillOfferedRequest
import io.github.giuliodalbono.swapit.model.entity.SkillOffered
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.stereotype.Component

@Component
class SkillOfferedMapper {

    fun toDto(skillOffered: SkillOffered): SkillOfferedDto {
        return SkillOfferedDto(
            id = skillOffered.id!!,
            userUid = skillOffered.user?.uid!!,
            skill = SkillMapper().toDto(skillOffered.skill!!),
            version = skillOffered.version,
            creationTime = skillOffered.creationTime!!,
            lastUpdate = skillOffered.lastUpdate!!
        )
    }

    fun toEntity(createRequest: CreateSkillOfferedRequest, user: User, skill: Skill): SkillOffered {
        return SkillOffered().apply {
            this.user = user
            this.skill = skill
        }
    }

    fun updateEntity(skillOffered: SkillOffered, updateRequest: UpdateSkillOfferedRequest, user: User, skill: Skill): SkillOffered {
        skillOffered.user = user
        skillOffered.skill = skill
        return skillOffered
    }
}
