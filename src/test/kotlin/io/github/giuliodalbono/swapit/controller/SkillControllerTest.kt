package io.github.giuliodalbono.swapit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.service.SkillService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(SkillController::class)
class SkillControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var skillService: SkillService

    private val testId = 1L
    private val testLabel = "Java Programming"
    private val testDescription = "Java programming skills"
    private val testMetadata = mapOf("level" to "intermediate", "category" to "programming")
    private val testDateTime = LocalDateTime.now()

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
    fun `getAllSkills should return all skills`() {
        // Given
        val skills = listOf(testSkillDto)
        `when`(skillService.findAll()).thenReturn(skills)

        // When & Then
        mockMvc.perform(get("/api/skills"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testId))
            .andExpect(jsonPath("$[0].label").value(testLabel))
            .andExpect(jsonPath("$[0].description").value(testDescription))
    }

    @Test
    fun `getSkillById should return skill when found`() {
        // Given
        `when`(skillService.findById(testId)).thenReturn(Optional.of(testSkillDto))

        // When & Then
        mockMvc.perform(get("/api/skills/{id}", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.label").value(testLabel))
            .andExpect(jsonPath("$.description").value(testDescription))
    }

    @Test
    fun `getSkillById should return 404 when not found`() {
        // Given
        `when`(skillService.findById(testId)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/skills/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getSkillByLabel should return skill when found`() {
        // Given
        `when`(skillService.findByLabel(testLabel)).thenReturn(Optional.of(testSkillDto))

        // When & Then
        mockMvc.perform(get("/api/skills/label/{label}", testLabel))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.label").value(testLabel))
            .andExpect(jsonPath("$.description").value(testDescription))
    }

    @Test
    fun `getSkillByLabel should return 404 when not found`() {
        // Given
        `when`(skillService.findByLabel(testLabel)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/skills/label/{label}", testLabel))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createSkill should create and return skill`() {
        // Given
        `when`(skillService.save(createSkillRequest)).thenReturn(testSkillDto)

        // When & Then
        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSkillRequest)))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.label").value(testLabel))
            .andExpect(jsonPath("$.description").value(testDescription))
    }

    @Test
    fun `updateSkill should update and return skill when found`() {
        // Given
        val updatedSkillDto = testSkillDto.copy(label = "Advanced Java")
        `when`(skillService.update(testId, updateSkillRequest)).thenReturn(updatedSkillDto)

        // When & Then
        mockMvc.perform(put("/api/skills/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSkillRequest)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testId))
            .andExpect(jsonPath("$.label").value("Advanced Java"))
    }

    @Test
    fun `updateSkill should return 404 when skill not found`() {
        // Given
        `when`(skillService.update(testId, updateSkillRequest)).thenThrow(IllegalArgumentException("Skill not found"))

        // When & Then
        mockMvc.perform(put("/api/skills/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSkillRequest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteSkill should delete skill when exists`() {
        // Given
        doNothing().`when`(skillService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/skills/{id}", testId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteSkill should return 404 when skill not found`() {
        // Given
        doThrow(IllegalArgumentException("Skill not found")).`when`(skillService).deleteById(testId)

        // When & Then
        mockMvc.perform(delete("/api/skills/{id}", testId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `checkSkillExists should return true when skill exists`() {
        // Given
        `when`(skillService.existsById(testId)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/skills/{id}/exists", testId))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `checkLabelExists should return true when label exists`() {
        // Given
        `when`(skillService.existsByLabel(testLabel)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/skills/label/{label}/exists", testLabel))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }
}
