package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.service.SkillService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skill Management", description = "APIs for managing skills and competencies")
class SkillController(private val skillService: SkillService) {

    @GetMapping
    @Operation(
        summary = "Get all skills",
        description = "Retrieves a list of all available skills in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of skills",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillDto>::class),
                )]
            )
        ]
    )
    fun getAllSkills(): ResponseEntity<List<SkillDto>> {
        val skills = skillService.findAll()
        return ResponseEntity.ok(skills)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get skill by ID",
        description = "Retrieves a specific skill by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getSkillById(
        @Parameter(
            description = "Unique identifier of the skill to retrieve",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<SkillDto> {
        return skillService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/label/{label}")
    @Operation(
        summary = "Get skill by label",
        description = "Retrieves a specific skill by its label/name"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getSkillByLabel(
        @Parameter(
            description = "Label/name of the skill to retrieve",
            example = "Java Programming",
            required = true
        ) @PathVariable label: String
    ): ResponseEntity<SkillDto> {
        return skillService.findByLabel(label)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    @Operation(
        summary = "Create new skill",
        description = "Creates a new skill in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Skill created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createSkill(
        @Parameter(
            description = "Skill data for creation",
            required = true
        ) @RequestBody createRequest: CreateSkillRequest
    ): ResponseEntity<SkillDto> {
        val savedSkill = skillService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update skill",
        description = "Updates an existing skill in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateSkill(
        @Parameter(
            description = "Unique identifier of the skill to update",
            example = "1",
            required = true
        ) @PathVariable id: Long,
        @Parameter(
            description = "Updated skill data",
            required = true
        ) @RequestBody updateRequest: UpdateSkillRequest
    ): ResponseEntity<SkillDto> {
        return try {
            val updatedSkill = skillService.update(id, updateRequest)
            ResponseEntity.ok(updatedSkill)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete skill",
        description = "Deletes a skill from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Skill deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSkill(
        @Parameter(
            description = "Unique identifier of the skill to delete",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Void> {
        return try {
            skillService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(
        summary = "Check skill existence",
        description = "Checks if a skill exists by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Skill Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkSkillExists(
        @Parameter(
            description = "Unique identifier of the skill to check",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/label/{label}/exists")
    @Operation(
        summary = "Check skill label existence",
        description = "Checks if a skill with the given label already exists"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Label existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Label Exists",
                        value = "{\"exists\": false}"
                    )]
                )]
            )
        ]
    )
    fun checkLabelExists(
        @Parameter(
            description = "Label/name of the skill to check",
            example = "Java Programming",
            required = true
        ) @PathVariable label: String
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsByLabel(label)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}