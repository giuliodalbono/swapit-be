package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.*
import io.github.giuliodalbono.swapit.mapper.SkillDesiredMapper
import io.github.giuliodalbono.swapit.mapper.SkillMapper
import io.github.giuliodalbono.swapit.mapper.SkillOfferedMapper
import io.github.giuliodalbono.swapit.model.entity.Skill
import io.github.giuliodalbono.swapit.model.repository.SkillDesiredRepository
import io.github.giuliodalbono.swapit.model.repository.SkillOfferedRepository
import io.github.giuliodalbono.swapit.model.repository.SkillRepository
import io.github.giuliodalbono.swapit.model.repository.UserRepository
import io.github.giuliodalbono.swapit.service.producer.SkillEventProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class SkillService(
    private val skillRepository: SkillRepository,
    private val skillMapper: SkillMapper,
    private val userRepository: UserRepository,
    private val skillEventProducer: SkillEventProducer,
    private val skillDesiredMapper: SkillDesiredMapper,
    private val skillOfferedMapper: SkillOfferedMapper,
    private val skillDesiredRepository: SkillDesiredRepository,
    private val skillOfferedRepository: SkillOfferedRepository
) {

    fun findAll(): List<SkillDto> = skillRepository.findAll().map { skillMapper.toDto(it) }

    fun findById(id: Long): Optional<SkillDto> = skillRepository.findById(id).map { skillMapper.toDto(it) }

    fun findByLabel(label: String): Optional<SkillDto> = skillRepository.findByLabel(label).map { skillMapper.toDto(it) }

    fun findAllByLabel(label: String): Set<SkillDto> = skillRepository.findAllByLabelContaining(label).map { skillMapper.toDto(it) }.toSet()

    fun save(createRequest: CreateSkillRequest): SkillDto {
        val skill = skillMapper.toEntity(createRequest)
        val savedSkill = skillRepository.save(skill)

        val skillDto = skillMapper.toDto(savedSkill)

        skillEventProducer.produceCreateSkillEvent(skillDto)

        return skillDto
    }

    fun update(id: Long, updateRequest: UpdateSkillRequest): SkillDto {
        val existingSkill = skillRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Skill with id $id not found") }

        val updatedSkill = skillMapper.updateEntity(existingSkill, updateRequest)
        val savedSkill = skillRepository.save(updatedSkill)

        val skillDto = skillMapper.toDto(savedSkill)

        skillEventProducer.produceUpdateSkillEvent(skillDto)

        return skillDto
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

    fun findAllSkillDesired(): List<SkillDesiredDto> = skillDesiredRepository.findAll().map { skillDesiredMapper.toDto(it) }

    fun findSkillDesiredById(id: Long): Optional<SkillDesiredDto> = skillDesiredRepository.findById(id).map { skillDesiredMapper.toDto(it) }

    fun findSkillDesiredByUserUid(userUid: String): List<SkillDesiredDto> = skillDesiredRepository.findByUserUid(userUid).map { skillDesiredMapper.toDto(it) }

    fun findSkillDesiredBySkillId(skillId: Long): List<SkillDesiredDto> {
        val skill = skillRepository.findById(skillId).orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }
        return skillDesiredRepository.findBySkill(skill).map { skillDesiredMapper.toDto(it) }
    }

    fun saveSkillDesired(createRequest: CreateSkillDesiredRequest): SkillDesiredDto {
        val user = userRepository.findById(createRequest.userUid)
            .orElseThrow { IllegalArgumentException("User with uid ${createRequest.userUid} not found") }
        val skill = skillRepository.findById(createRequest.skillId)
            .orElseThrow { IllegalArgumentException("Skill with id ${createRequest.skillId} not found") }

        if (skillDesiredRepository.existsByUserAndSkill(user, skill)) {
            throw IllegalArgumentException("User ${createRequest.userUid} already desires skill ${createRequest.skillId}")
        }

        val skillDesired = skillDesiredMapper.toEntity(createRequest, user, skill)
        val savedSkillDesired = skillDesiredRepository.save(skillDesired)
        return skillDesiredMapper.toDto(savedSkillDesired)
    }

    fun updateSkillDesired(id: Long, updateRequest: UpdateSkillDesiredRequest): SkillDesiredDto {
        val existingSkillDesired = skillDesiredRepository.findById(id)
            .orElseThrow { IllegalArgumentException("SkillDesired with id $id not found") }

        val user = userRepository.findById(updateRequest.userUid)
            .orElseThrow { IllegalArgumentException("User with uid ${updateRequest.userUid} not found") }
        val skill = skillRepository.findById(updateRequest.skillId)
            .orElseThrow { IllegalArgumentException("Skill with id ${updateRequest.skillId} not found") }

        val updatedSkillDesired = skillDesiredMapper.updateEntity(existingSkillDesired, updateRequest, user, skill)
        val savedSkillDesired = skillDesiredRepository.save(updatedSkillDesired)
        return skillDesiredMapper.toDto(savedSkillDesired)
    }

    fun deleteSkillDesiredById(id: Long) {
        if (!skillDesiredRepository.existsById(id)) {
            throw IllegalArgumentException("SkillDesired with id $id not found")
        }
        skillDesiredRepository.deleteById(id)
    }

    fun deleteSkillDesiredByUserAndSkill(userUid: String, skillId: Long) {
        userRepository.findById(userUid)
            .orElseThrow { IllegalArgumentException("User with uid $userUid not found") }
        skillRepository.findById(skillId)
            .orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }

        if (skillDesiredRepository.deleteByUserUidAndSkillId(userUid, skillId) <= 0) {
            throw IllegalArgumentException("SkillDesired with user uid $userUid and skill id $skillId not found")
        }
    }

    fun existsSkillDesiredById(id: Long): Boolean = skillDesiredRepository.existsById(id)

    fun existsSkillDesiredByUserAndSkill(userUid: String, skillId: Long): Boolean {
        val user = userRepository.findById(userUid)
            .orElseThrow { IllegalArgumentException("User with uid $userUid not found") }
        val skill = skillRepository.findById(skillId)
            .orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }
        return skillDesiredRepository.existsByUserAndSkill(user, skill)
    }

    fun findAllSkillOffered(): List<SkillOfferedDto> = skillOfferedRepository.findAll().map { skillOfferedMapper.toDto(it) }

    fun findSkillOfferedById(id: Long): Optional<SkillOfferedDto> = skillOfferedRepository.findById(id).map { skillOfferedMapper.toDto(it) }

    fun findSkillOfferedByUserUid(userUid: String): List<SkillOfferedDto> = skillOfferedRepository.findByUserUid(userUid).map { skillOfferedMapper.toDto(it) }

    fun findSkillOfferedBySkillId(skillId: Long): List<SkillOfferedDto> {
        val skill = skillRepository.findById(skillId).orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }
        return skillOfferedRepository.findBySkill(skill).map { skillOfferedMapper.toDto(it) }
    }

    fun saveSkillOffered(createRequest: CreateSkillOfferedRequest): SkillOfferedDto {
        val user = userRepository.findById(createRequest.userUid)
            .orElseThrow { IllegalArgumentException("User with uid ${createRequest.userUid} not found") }
        val skill = skillRepository.findById(createRequest.skillId)
            .orElseThrow { IllegalArgumentException("Skill with id ${createRequest.skillId} not found") }

        if (skillOfferedRepository.existsByUserAndSkill(user, skill)) {
            throw IllegalArgumentException("User ${createRequest.userUid} already offers skill ${createRequest.skillId}")
        }

        val skillOffered = skillOfferedMapper.toEntity(createRequest, user, skill)
        val savedSkillOffered = skillOfferedRepository.save(skillOffered)
        return skillOfferedMapper.toDto(savedSkillOffered)
    }

    fun updateSkillOffered(id: Long, updateRequest: UpdateSkillOfferedRequest): SkillOfferedDto {
        val existingSkillOffered = skillOfferedRepository.findById(id)
            .orElseThrow { IllegalArgumentException("SkillOffered with id $id not found") }

        val user = userRepository.findById(updateRequest.userUid)
            .orElseThrow { IllegalArgumentException("User with uid ${updateRequest.userUid} not found") }
        val skill = skillRepository.findById(updateRequest.skillId)
            .orElseThrow { IllegalArgumentException("Skill with id ${updateRequest.skillId} not found") }

        val updatedSkillOffered = skillOfferedMapper.updateEntity(existingSkillOffered, updateRequest, user, skill)
        val savedSkillOffered = skillOfferedRepository.save(updatedSkillOffered)
        return skillOfferedMapper.toDto(savedSkillOffered)
    }

    fun deleteSkillOfferedById(id: Long) {
        if (!skillOfferedRepository.existsById(id)) {
            throw IllegalArgumentException("SkillOffered with id $id not found")
        }
        skillOfferedRepository.deleteById(id)
    }

    fun deleteSkillOfferedByUserAndSkill(userUid: String, skillId: Long) {
        userRepository.findById(userUid)
            .orElseThrow { IllegalArgumentException("User with uid $userUid not found") }
        skillRepository.findById(skillId)
            .orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }

        if (skillOfferedRepository.deleteByUserUidAndSkillId(userUid, skillId) <= 0) {
            throw IllegalArgumentException("SkillOffered with user uid $userUid and skill id $skillId not found")
        }
    }

    fun existsSkillOfferedById(id: Long): Boolean = skillOfferedRepository.existsById(id)

    fun existsSkillOfferedByUserAndSkill(userUid: String, skillId: Long): Boolean {
        val user = userRepository.findById(userUid)
            .orElseThrow { IllegalArgumentException("User with uid $userUid not found") }
        val skill = skillRepository.findById(skillId)
            .orElseThrow { IllegalArgumentException("Skill with id $skillId not found") }
        return skillOfferedRepository.existsByUserAndSkill(user, skill)
    }
}