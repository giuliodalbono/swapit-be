package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.*
import io.github.giuliodalbono.swapit.mapper.SkillDesiredMapper
import io.github.giuliodalbono.swapit.mapper.SkillMapper
import io.github.giuliodalbono.swapit.mapper.SkillOfferedMapper
import io.github.giuliodalbono.swapit.model.entity.Skill
import io.github.giuliodalbono.swapit.model.entity.SkillDesired
import io.github.giuliodalbono.swapit.model.entity.SkillOffered
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.repository.SkillDesiredRepository
import io.github.giuliodalbono.swapit.model.repository.SkillOfferedRepository
import io.github.giuliodalbono.swapit.model.repository.SkillRepository
import io.github.giuliodalbono.swapit.model.repository.UserRepository
import io.github.giuliodalbono.swapit.service.producer.SkillEventProducer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class SkillServiceTest {

    @Mock
    private lateinit var skillRepository: SkillRepository

    @Mock
    private lateinit var skillMapper: SkillMapper

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var skillEventProducer: SkillEventProducer

    @Mock
    private lateinit var skillDesiredMapper: SkillDesiredMapper

    @Mock
    private lateinit var skillOfferedMapper: SkillOfferedMapper

    @Mock
    private lateinit var skillDesiredRepository: SkillDesiredRepository

    @Mock
    private lateinit var skillOfferedRepository: SkillOfferedRepository

    @InjectMocks
    private lateinit var skillService: SkillService

    private val testId = 1L
    private val testUserUid = "user123"
    private val testLabel = "Java Programming"
    private val testDescription = "Java programming skills"
    private val testMetadata = mapOf("level" to "intermediate", "category" to "programming")
    private val testDateTime = LocalDateTime.now()

    private val testUser = User().apply {
        uid = testUserUid
        email = "test@example.com"
        username = "testUser"
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
    }

    private val testSkill = Skill().apply {
        id = testId
        label = testLabel
        description = testDescription
        metadata = testMetadata.toMutableMap()
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
    }

    private val testSkillDesired = SkillDesired().apply {
        id = testId
        user = testUser
        skill = testSkill
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
    }

    private val testSkillOffered = SkillOffered().apply {
        id = testId
        user = testUser
        skill = testSkill
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

    private val testSkillDesiredDto = SkillDesiredDto(
        id = testId,
        userUid = testUserUid,
        skill = testSkillDto,
        version = 0L,
        creationTime = testDateTime,
        lastUpdate = testDateTime
    )

    private val testSkillOfferedDto = SkillOfferedDto(
        id = testId,
        userUid = testUserUid,
        skill = testSkillDto,
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

    private val createSkillDesiredRequest = CreateSkillDesiredRequest(
        userUid = testUserUid,
        skillId = testId
    )

    private val updateSkillDesiredRequest = UpdateSkillDesiredRequest(
        userUid = testUserUid,
        skillId = testId
    )

    private val createSkillOfferedRequest = CreateSkillOfferedRequest(
        userUid = testUserUid,
        skillId = testId
    )

    private val updateSkillOfferedRequest = UpdateSkillOfferedRequest(
        userUid = testUserUid,
        skillId = testId
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
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
        verify(skillEventProducer).produceCreateSkillEvent(testSkillDto)
    }

    @Test
    fun `save should not produce event when skill creation fails`() {
        // Given
        `when`(skillMapper.toEntity(createSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenThrow(RuntimeException("Database error"))

        // When & Then
        assertThrows<RuntimeException> {
            skillService.save(createSkillRequest)
        }

        verify(skillMapper).toEntity(createSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper, never()).toDto(testSkill)
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
    }

    @Test
    fun `save should not produce event when DTO mapping fails`() {
        // Given
        `when`(skillMapper.toEntity(createSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenReturn(testSkill)
        `when`(skillMapper.toDto(testSkill)).thenThrow(RuntimeException("Mapping error"))

        // When & Then
        assertThrows<RuntimeException> {
            skillService.save(createSkillRequest)
        }
        
        verify(skillMapper).toEntity(createSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper).toDto(testSkill)
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
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
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
        verify(skillEventProducer).produceUpdateSkillEvent(any())
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
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
    }

    @Test
    fun `update should not produce event when skill update fails`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillMapper.updateEntity(testSkill, updateSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenThrow(RuntimeException("Database error"))

        // When & Then
        assertThrows<RuntimeException> {
            skillService.update(testId, updateSkillRequest)
        }

        verify(skillRepository).findById(testId)
        verify(skillMapper).updateEntity(testSkill, updateSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper, never()).toDto(testSkill)
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
    }

    @Test
    fun `update should not produce event when DTO mapping fails`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillMapper.updateEntity(testSkill, updateSkillRequest)).thenReturn(testSkill)
        `when`(skillRepository.save(testSkill)).thenReturn(testSkill)
        `when`(skillMapper.toDto(testSkill)).thenThrow(RuntimeException("Mapping error"))

        // When & Then
        assertThrows<RuntimeException> {
            skillService.update(testId, updateSkillRequest)
        }

        verify(skillRepository).findById(testId)
        verify(skillMapper).updateEntity(testSkill, updateSkillRequest)
        verify(skillRepository).save(testSkill)
        verify(skillMapper).toDto(testSkill)
        verify(skillEventProducer, never()).produceUpdateSkillEvent(any())
        verify(skillEventProducer, never()).produceCreateSkillEvent(any())
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

    @Test
    fun `findEntityById should return skill entity when found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))

        // When
        val result = skillService.findEntityById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSkill)
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `findEntityById should return empty when not found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When
        val result = skillService.findEntityById(testId)

        // Then
        assert(!result.isPresent)
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `findAllSkillDesired should return all skills desired`() {
        // Given
        val skillsDesired = listOf(testSkillDesired)
        val skillDesiredDtos = listOf(testSkillDesiredDto)
        `when`(skillDesiredRepository.findAll()).thenReturn(skillsDesired)
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.findAllSkillDesired()

        // Then
        assert(result == skillDesiredDtos)
        verify(skillDesiredRepository).findAll()
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `findSkillDesiredById should return skill desired when found`() {
        // Given
        `when`(skillDesiredRepository.findById(testId)).thenReturn(Optional.of(testSkillDesired))
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.findSkillDesiredById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSkillDesiredDto)
        verify(skillDesiredRepository).findById(testId)
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `findSkillDesiredById should return empty when not found`() {
        // Given
        `when`(skillDesiredRepository.findById(testId)).thenReturn(Optional.empty())

        // When
        val result = skillService.findSkillDesiredById(testId)

        // Then
        assert(!result.isPresent)
        verify(skillDesiredRepository).findById(testId)
        verify(skillDesiredMapper, never()).toDto(testSkillDesired)
    }

    @Test
    fun `findSkillDesiredByUserUid should return skills desired by user`() {
        // Given
        val skillsDesired = listOf(testSkillDesired)
        val skillDesiredDtos = listOf(testSkillDesiredDto)
        `when`(skillDesiredRepository.findByUserUid(testUserUid)).thenReturn(skillsDesired)
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.findSkillDesiredByUserUid(testUserUid)

        // Then
        assert(result == skillDesiredDtos)
        verify(skillDesiredRepository).findByUserUid(testUserUid)
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `findSkillDesiredBySkillId should return users who desire skill`() {
        // Given
        val skillsDesired = listOf(testSkillDesired)
        val skillDesiredDtos = listOf(testSkillDesiredDto)
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredRepository.findBySkill(testSkill)).thenReturn(skillsDesired)
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.findSkillDesiredBySkillId(testId)

        // Then
        assert(result == skillDesiredDtos)
        verify(skillRepository).findById(testId)
        verify(skillDesiredRepository).findBySkill(testSkill)
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `findSkillDesiredBySkillId should throw exception when skill not found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.findSkillDesiredBySkillId(testId)
        }
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `saveSkillDesired should create and return skill desired`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(false)
        `when`(skillDesiredMapper.toEntity(createSkillDesiredRequest, testUser, testSkill)).thenReturn(testSkillDesired)
        `when`(skillDesiredRepository.save(testSkillDesired)).thenReturn(testSkillDesired)
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.saveSkillDesired(createSkillDesiredRequest)

        // Then
        assert(result == testSkillDesiredDto)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillDesiredRepository).existsByUserAndSkill(testUser, testSkill)
        verify(skillDesiredMapper).toEntity(createSkillDesiredRequest, testUser, testSkill)
        verify(skillDesiredRepository).save(testSkillDesired)
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `saveSkillDesired should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillDesired(createSkillDesiredRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository, never()).findById(any())
    }

    @Test
    fun `saveSkillDesired should throw exception when skill not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillDesired(createSkillDesiredRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `saveSkillDesired should throw exception when combination already exists`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(true)

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillDesired(createSkillDesiredRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillDesiredRepository).existsByUserAndSkill(testUser, testSkill)
    }

    @Test
    fun `updateSkillDesired should update and return skill desired`() {
        // Given
        `when`(skillDesiredRepository.findById(testId)).thenReturn(Optional.of(testSkillDesired))
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredMapper.updateEntity(testSkillDesired, updateSkillDesiredRequest, testUser, testSkill)).thenReturn(testSkillDesired)
        `when`(skillDesiredRepository.save(testSkillDesired)).thenReturn(testSkillDesired)
        `when`(skillDesiredMapper.toDto(testSkillDesired)).thenReturn(testSkillDesiredDto)

        // When
        val result = skillService.updateSkillDesired(testId, updateSkillDesiredRequest)

        // Then
        assert(result == testSkillDesiredDto)
        verify(skillDesiredRepository).findById(testId)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillDesiredMapper).updateEntity(testSkillDesired, updateSkillDesiredRequest, testUser, testSkill)
        verify(skillDesiredRepository).save(testSkillDesired)
        verify(skillDesiredMapper).toDto(testSkillDesired)
    }

    @Test
    fun `updateSkillDesired should throw exception when skill desired not found`() {
        // Given
        `when`(skillDesiredRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.updateSkillDesired(testId, updateSkillDesiredRequest)
        }
        verify(skillDesiredRepository).findById(testId)
    }

    @Test
    fun `deleteSkillDesiredById should delete skill desired when exists`() {
        // Given
        `when`(skillDesiredRepository.existsById(testId)).thenReturn(true)

        // When
        skillService.deleteSkillDesiredById(testId)

        // Then
        verify(skillDesiredRepository).existsById(testId)
        verify(skillDesiredRepository).deleteById(testId)
    }

    @Test
    fun `deleteSkillDesiredById should throw exception when skill desired not found`() {
        // Given
        `when`(skillDesiredRepository.existsById(testId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.deleteSkillDesiredById(testId)
        }
        verify(skillDesiredRepository).existsById(testId)
        verify(skillDesiredRepository, never()).deleteById(any())
    }

    @Test
    fun `deleteSkillDesiredByUserAndSkill should delete skill desired`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredRepository.deleteByUserUidAndSkillId(testUserUid, testId)).thenReturn(1)

        // When
        skillService.deleteSkillDesiredByUserAndSkill(testUserUid, testId)

        // Then
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillDesiredRepository).deleteByUserUidAndSkillId(testUser.uid!!, testSkill.id!!)
    }

    @Test
    fun `deleteSkillDesiredByUserAndSkill should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.deleteSkillDesiredByUserAndSkill(testUserUid, testId)
        }
        verify(userRepository).findById(testUserUid)
    }

    @Test
    fun `existsSkillDesiredById should return true when skill desired exists`() {
        // Given
        `when`(skillDesiredRepository.existsById(testId)).thenReturn(true)

        // When
        val result = skillService.existsSkillDesiredById(testId)

        // Then
        assert(result)
        verify(skillDesiredRepository).existsById(testId)
    }

    @Test
    fun `existsSkillDesiredByUserAndSkill should return true when combination exists`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillDesiredRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(true)

        // When
        val result = skillService.existsSkillDesiredByUserAndSkill(testUserUid, testId)

        // Then
        assert(result)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillDesiredRepository).existsByUserAndSkill(testUser, testSkill)
    }

    @Test
    fun `existsSkillDesiredByUserAndSkill should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.existsSkillDesiredByUserAndSkill(testUserUid, testId)
        }
        verify(userRepository).findById(testUserUid)
    }

    @Test
    fun `findAllSkillOffered should return all skills offered`() {
        // Given
        val skillsOffered = listOf(testSkillOffered)
        val skillOfferedDtos = listOf(testSkillOfferedDto)
        `when`(skillOfferedRepository.findAll()).thenReturn(skillsOffered)
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.findAllSkillOffered()

        // Then
        assert(result == skillOfferedDtos)
        verify(skillOfferedRepository).findAll()
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `findSkillOfferedById should return skill offered when found`() {
        // Given
        `when`(skillOfferedRepository.findById(testId)).thenReturn(Optional.of(testSkillOffered))
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.findSkillOfferedById(testId)

        // Then
        assert(result.isPresent)
        assert(result.get() == testSkillOfferedDto)
        verify(skillOfferedRepository).findById(testId)
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `findSkillOfferedById should return empty when not found`() {
        // Given
        `when`(skillOfferedRepository.findById(testId)).thenReturn(Optional.empty())

        // When
        val result = skillService.findSkillOfferedById(testId)

        // Then
        assert(!result.isPresent)
        verify(skillOfferedRepository).findById(testId)
        verify(skillOfferedMapper, never()).toDto(testSkillOffered)
    }

    @Test
    fun `findSkillOfferedByUserUid should return skills offered by user`() {
        // Given
        val skillsOffered = listOf(testSkillOffered)
        val skillOfferedDtos = listOf(testSkillOfferedDto)
        `when`(skillOfferedRepository.findByUserUid(testUserUid)).thenReturn(skillsOffered)
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.findSkillOfferedByUserUid(testUserUid)

        // Then
        assert(result == skillOfferedDtos)
        verify(skillOfferedRepository).findByUserUid(testUserUid)
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `findSkillOfferedBySkillId should return users who offer skill`() {
        // Given
        val skillsOffered = listOf(testSkillOffered)
        val skillOfferedDtos = listOf(testSkillOfferedDto)
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedRepository.findBySkill(testSkill)).thenReturn(skillsOffered)
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.findSkillOfferedBySkillId(testId)

        // Then
        assert(result == skillOfferedDtos)
        verify(skillRepository).findById(testId)
        verify(skillOfferedRepository).findBySkill(testSkill)
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `findSkillOfferedBySkillId should throw exception when skill not found`() {
        // Given
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.findSkillOfferedBySkillId(testId)
        }
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `saveSkillOffered should create and return skill offered`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(false)
        `when`(skillOfferedMapper.toEntity(createSkillOfferedRequest, testUser, testSkill)).thenReturn(testSkillOffered)
        `when`(skillOfferedRepository.save(testSkillOffered)).thenReturn(testSkillOffered)
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.saveSkillOffered(createSkillOfferedRequest)

        // Then
        assert(result == testSkillOfferedDto)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillOfferedRepository).existsByUserAndSkill(testUser, testSkill)
        verify(skillOfferedMapper).toEntity(createSkillOfferedRequest, testUser, testSkill)
        verify(skillOfferedRepository).save(testSkillOffered)
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `saveSkillOffered should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillOffered(createSkillOfferedRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository, never()).findById(any())
    }

    @Test
    fun `saveSkillOffered should throw exception when skill not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillOffered(createSkillOfferedRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
    }

    @Test
    fun `saveSkillOffered should throw exception when combination already exists`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(true)

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.saveSkillOffered(createSkillOfferedRequest)
        }
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillOfferedRepository).existsByUserAndSkill(testUser, testSkill)
    }

    @Test
    fun `updateSkillOffered should update and return skill offered`() {
        // Given
        `when`(skillOfferedRepository.findById(testId)).thenReturn(Optional.of(testSkillOffered))
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedMapper.updateEntity(testSkillOffered, updateSkillOfferedRequest, testUser, testSkill)).thenReturn(testSkillOffered)
        `when`(skillOfferedRepository.save(testSkillOffered)).thenReturn(testSkillOffered)
        `when`(skillOfferedMapper.toDto(testSkillOffered)).thenReturn(testSkillOfferedDto)

        // When
        val result = skillService.updateSkillOffered(testId, updateSkillOfferedRequest)

        // Then
        assert(result == testSkillOfferedDto)
        verify(skillOfferedRepository).findById(testId)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillOfferedMapper).updateEntity(testSkillOffered, updateSkillOfferedRequest, testUser, testSkill)
        verify(skillOfferedRepository).save(testSkillOffered)
        verify(skillOfferedMapper).toDto(testSkillOffered)
    }

    @Test
    fun `updateSkillOffered should throw exception when skill offered not found`() {
        // Given
        `when`(skillOfferedRepository.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.updateSkillOffered(testId, updateSkillOfferedRequest)
        }
        verify(skillOfferedRepository).findById(testId)
    }

    @Test
    fun `deleteSkillOfferedById should delete skill offered when exists`() {
        // Given
        `when`(skillOfferedRepository.existsById(testId)).thenReturn(true)

        // When
        skillService.deleteSkillOfferedById(testId)

        // Then
        verify(skillOfferedRepository).existsById(testId)
        verify(skillOfferedRepository).deleteById(testId)
    }

    @Test
    fun `deleteSkillOfferedById should throw exception when skill offered not found`() {
        // Given
        `when`(skillOfferedRepository.existsById(testId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.deleteSkillOfferedById(testId)
        }
        verify(skillOfferedRepository).existsById(testId)
        verify(skillOfferedRepository, never()).deleteById(any())
    }

    @Test
    fun `deleteSkillOfferedByUserAndSkill should delete skill offered`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedRepository.deleteByUserUidAndSkillId(testUserUid, testId)).thenReturn(1)

        // When
        skillService.deleteSkillOfferedByUserAndSkill(testUserUid, testId)

        // Then
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillOfferedRepository).deleteByUserUidAndSkillId(testUser.uid!!, testSkill.id!!)
    }

    @Test
    fun `deleteSkillOfferedByUserAndSkill should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.deleteSkillOfferedByUserAndSkill(testUserUid, testId)
        }
        verify(userRepository).findById(testUserUid)
    }

    @Test
    fun `existsSkillOfferedById should return true when skill offered exists`() {
        // Given
        `when`(skillOfferedRepository.existsById(testId)).thenReturn(true)

        // When
        val result = skillService.existsSkillOfferedById(testId)

        // Then
        assert(result)
        verify(skillOfferedRepository).existsById(testId)
    }

    @Test
    fun `existsSkillOfferedByUserAndSkill should return true when combination exists`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.of(testUser))
        `when`(skillRepository.findById(testId)).thenReturn(Optional.of(testSkill))
        `when`(skillOfferedRepository.existsByUserAndSkill(testUser, testSkill)).thenReturn(true)

        // When
        val result = skillService.existsSkillOfferedByUserAndSkill(testUserUid, testId)

        // Then
        assert(result)
        verify(userRepository).findById(testUserUid)
        verify(skillRepository).findById(testId)
        verify(skillOfferedRepository).existsByUserAndSkill(testUser, testSkill)
    }

    @Test
    fun `existsSkillOfferedByUserAndSkill should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUserUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            skillService.existsSkillOfferedByUserAndSkill(testUserUid, testId)
        }
        verify(userRepository).findById(testUserUid)
    }
}