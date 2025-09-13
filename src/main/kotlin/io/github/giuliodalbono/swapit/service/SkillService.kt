package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.mapper.SkillMapper
import io.github.giuliodalbono.swapit.model.entity.Skill
import io.github.giuliodalbono.swapit.model.repository.SkillRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class SkillService(
    private val skillRepository: SkillRepository,
    private val skillMapper: SkillMapper
) {

    fun findAll(): List<SkillDto> = skillRepository.findAll().map { skillMapper.toDto(it) }

    fun findById(id: Long): Optional<SkillDto> = skillRepository.findById(id).map { skillMapper.toDto(it) }

    fun findByLabel(label: String): Optional<SkillDto> = skillRepository.findByLabel(label).map { skillMapper.toDto(it) }

    fun save(createRequest: CreateSkillRequest): SkillDto {
        val skill = skillMapper.toEntity(createRequest)
        val savedSkill = skillRepository.save(skill)
        return skillMapper.toDto(savedSkill)
    }

    fun update(id: Long, updateRequest: UpdateSkillRequest): SkillDto {
        val existingSkill = skillRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Skill with id $id not found") }
        
        val updatedSkill = skillMapper.updateEntity(existingSkill, updateRequest)
        val savedSkill = skillRepository.save(updatedSkill)
        return skillMapper.toDto(savedSkill)
    }

    fun deleteById(id: Long) {
        if (!skillRepository.existsById(id)) {
            throw IllegalArgumentException("Skill with id $id not found")
        }
        skillRepository.deleteById(id)
    }

    fun existsById(id: Long): Boolean = skillRepository.existsById(id)

    fun existsByLabel(label: String): Boolean = skillRepository.existsByLabel(label)

    fun findEntityById(id: Long): Optional<Skill> = skillRepository.findById(id)
}