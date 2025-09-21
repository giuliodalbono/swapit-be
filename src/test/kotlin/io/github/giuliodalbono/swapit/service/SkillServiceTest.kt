package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.mapper.SkillMapper
import io.github.giuliodalbono.swapit.model.entity.Skill
import io.github.giuliodalbono.swapit.model.repository.SkillRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class SkillServiceTest {

    @Mock
    private lateinit var skillRepository: SkillRepository

    @Mock
    private lateinit var skillMapper: SkillMapper

    @InjectMocks
    private lateinit var skillService: SkillService

    private val testId = 1L
    private val testLabel = "Java Programming"
    private val testDescription = "Java programming skills"
    private val testMetadata = mapOf("level" to "intermediate", "category" to "programming")
    private val testDateTime = LocalDateTime.now()

    private val testSkill = Skill().apply {
        id = testId
        label = testLabel
        description = testDescription
        metadata = testMetadata.toMutableMap()
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
    }

    private val testSkillDto = SkillDto(
        id = testId,
        label = testLabel,
        metadata = testMetadata,
        description = testDescription,
        version = 0L,
        creationTime = testDateTime,
        lastUpdate = testDateTime
    )

    private val createSkillRequest = CreateSkillRequest(
        label = testLabel,
        metadata = testMetadata,
        description = testDescription
    )

    private val updateSkillRequest = UpdateSkillRequest(
        label = "Advanced Java",
        metadata = mapOf("level" to "advanced"),
        description = "Advanced Java programming"
    )

    @Test
    fun `findAll should return all skills`() {
        // Given
        val skills = listOf(testSkill)
        val skillDtos = listOf(testSkillDto)
        `when`(skillRepository.findAll()).thenReturn(skills)
        `when`(skillMapper.toDto(testSkill)).thenReturn(testSkillDto)

        // When
        val result = skillService.findAll()

        // Then
        assert(result == skillDtos)
        verify(skillRepository).findAll()
        verify(skillMapper).toDto(testSkill)
    }

    @Test
    fun `findById should return skill when found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillMapper.toDto(testSkill)).thenReturn(testSkillDto)

        // When
        val result = skillService.findById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSkillDto)
        verify(skillRepository).findById(testId)
        verify(skillMapper).toDto(testSkill)
    }

    @Test
    fun `findById should return empty when not found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When
        val result = skillService.findById(testId)

        // Then
        assert(!result.isPresent)
        verify(skillRepository).findById(testId)
        verify(skillMapper, never()).toDto(testSkill)
    }

    @Test
    fun `findByLabel should return skill when found`() {
        // Given
        `when`(skillRepository.findByLabel(testLabel)).thenReturn(Optional.of(testSkill))
        `when`(skillMapper.toDto(testSkill)).thenReturn(testSkillDto)

        // When
        val result = skillService.findByLabel(testLabel)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSkillDto)
        verify(skillRepository).findByLabel(testLabel)
        verify(skillMapper).toDto(testSkill)
    }

    @Test
    fun `save should create and return skill`() {
        // Given
        `when`(skillMapper.toEntity(createSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenReturn(testSkill)
        `when`(skillMapper.toDto(testSkill)).thenReturn(testSkillDto)

        // When
        val result = skillService.save(createSkillRequest)

        // Then
        assert(result == testSkillDto)
        verify(skillMapper).toEntity(createSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper).toDto(testSkill)
    }

    @Test
    fun `update should update and return skill when found`() {
        // Given
        val updatedSkillDto = testSkillDto.copy(label = "Advanced Java")
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillMapper.updateEntity(testSkill, updateSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenReturn(testSkill)
        `when`(skillMapper.toDto(testSkill)).thenReturn(updatedSkillDto)

        // When
        val result = skillService.update(testId, updateSkillRequest)

        // Then
        assert(result == updatedSkillDto)
        verify(skillRepository).findById(testId)
        verify(skillMapper).updateEntity(testSkill, updateSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper).toDto(testSkill)
    }

    @Test
    fun `update should throw exception when skill not found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.update(testId, updateSkillRequest)
        }
        verify(skillRepository).findById(testId)
        verify(skillMapper, never()).updateEntity(testSkill, updateSkillRequest)
        verify(skillRepository, never()).save(testSkill)
    }

    @Test
    fun `deleteById should delete skill when exists`() {
        // Given
        `when`(skillRepository.existsById(testId)).thenReturn(true)

        // When
        skillService.deleteById(testId)

        // Then
        verify(skillRepository).existsById(testId)
        verify(skillRepository).deleteById(testId)
    }

    @Test
    fun `deleteById should throw exception when skill not found`() {
        // Given
        `when`(skillRepository.existsById(testId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.deleteById(testId)
        }
        verify(skillRepository).existsById(testId)
        verify(skillRepository, never()).deleteById(any())
    }

    @Test
    fun `existsById should return true when skill exists`() {
        // Given
        `when`(skillRepository.existsById(testId)).thenReturn(true)

        // When
        val result = skillService.existsById(testId)

        // Then
        assert(result)
        verify(skillRepository).existsById(testId)
    }

    @Test
    fun `existsByLabel should return true when label exists`() {
        // Given
        `when`(skillRepository.existsByLabel(testLabel)).thenReturn(true)

        // When
        val result = skillService.existsByLabel(testLabel)

        // Then
        assert(result)
        verify(skillRepository).existsByLabel(testLabel)
    }
}
