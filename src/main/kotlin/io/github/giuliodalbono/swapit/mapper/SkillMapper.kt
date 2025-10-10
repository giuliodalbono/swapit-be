package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.model.entity.Skill
import org.springframework.stereotype.Component

@Component
class SkillMapper {

    fun toDto(skill: Skill): SkillDto {
        return SkillDto(
            id = skill.id!!,
            label = skill.label!!,
            metadata = skill.metadata,
            description = skill.description,
            version = skill.version,
            creationTime = skill.creationTime!!,
            lastUpdate = skill.lastUpdate!!
        )
    }

    fun toEntity(createRequest: CreateSkillRequest): Skill {
        return Skill().apply {
            label = createRequest.label
            metadata = createRequest.metadata?.toMutableMap()
            description = createRequest.description
        }
    }

    fun updateEntity(skill: Skill, updateRequest: UpdateSkillRequest): Skill {
        skill.label = updateRequest.label
        skill.metadata = updateRequest.metadata?.toMutableMap()
        skill.description = updateRequest.description
        return skill
    }
}
