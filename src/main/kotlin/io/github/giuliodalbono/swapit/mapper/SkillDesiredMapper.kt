package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateSkillDesiredRequest
import io.github.giuliodalbono.swapit.dto.SkillDesiredDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillDesiredRequest
import io.github.giuliodalbono.swapit.model.entity.SkillDesired
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.stereotype.Component

@Component
class SkillDesiredMapper {

    fun toDto(skillDesired: SkillDesired): SkillDesiredDto {
        return SkillDesiredDto(
            id = skillDesired.id!!,
            userUid = skillDesired.user?.uid!!,
            skill = SkillMapper().toDto(skillDesired.skill!!),
            version = skillDesired.version,
            creationTime = skillDesired.creationTime!!,
            lastUpdate = skillDesired.lastUpdate!!
        )
    }

    fun toEntity(createRequest: CreateSkillDesiredRequest, user: User, skill: Skill): SkillDesired {
        return SkillDesired().apply {
            this.user = user
            this.skill = skill
        }
    }

    fun updateEntity(skillDesired: SkillDesired, updateRequest: UpdateSkillDesiredRequest, user: User, skill: Skill): SkillDesired {
        skillDesired.user = user
        skillDesired.skill = skill
        return skillDesired
    }
}
