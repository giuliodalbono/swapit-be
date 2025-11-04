package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.*
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

    @GetMapping("/labels/{label}")
    @Operation(
        summary = "Get skills by label like",
        description = "Retrieves all skills by label/name"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skills found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillDto>::class)
                )]
            )
        ]
    )
    fun getAllSkillsByLabel(
        @Parameter(
            description = "Label/name of the skill to retrieve",
            example = "Java Programming",
            required = true
        ) @PathVariable label: String
    ): Set<SkillDto> {
        return skillService.findAllByLabel(label)
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

    @GetMapping("/desired")
    @Operation(
        summary = "Get all skills desired",
        description = "Retrieves a list of all skills desired by users"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of skills desired",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillDesiredDto>::class)
                )]
            )
        ]
    )
    fun getAllSkillsDesired(): ResponseEntity<List<SkillDesiredDto>> {
        val skillsDesired = skillService.findAllSkillDesired()
        return ResponseEntity.ok(skillsDesired)
    }

    @GetMapping("/desired/{id}")
    @Operation(
        summary = "Get skill desired by ID",
        description = "Retrieves a specific skill desired by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill desired found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDesiredDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill desired not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getSkillDesiredById(
        @Parameter(
            description = "Unique identifier of the skill desired to retrieve",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<SkillDesiredDto> {
        return skillService.findSkillDesiredById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/desired/user/{userUid}")
    @Operation(
        summary = "Get skills desired by user",
        description = "Retrieves all skills desired by a specific user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved skills desired by user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillDesiredDto>::class)
                )]
            )
        ]
    )
    fun getSkillsDesiredByUser(
        @Parameter(
            description = "User UID to retrieve desired skills for",
            example = "user123",
            required = true
        ) @PathVariable userUid: String
    ): ResponseEntity<List<SkillDesiredDto>> {
        val skillsDesired = skillService.findSkillDesiredByUserUid(userUid)
        return ResponseEntity.ok(skillsDesired)
    }

    @GetMapping("/desired/skill/{skillId}")
    @Operation(
        summary = "Get users who desire a skill",
        description = "Retrieves all users who desire a specific skill"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users who desire the skill",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillDesiredDto>::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getUsersWhoDesireSkill(
        @Parameter(
            description = "Skill ID to find users who desire it",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<List<SkillDesiredDto>> {
        return try {
            val skillsDesired = skillService.findSkillDesiredBySkillId(skillId)
            ResponseEntity.ok(skillsDesired)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/desired")
    @Operation(
        summary = "Create new skill desired",
        description = "Creates a new skill desired by a user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Skill desired created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDesiredDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data or user/skill not found or already exists",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createSkillDesired(
        @Parameter(
            description = "Skill desired data for creation",
            required = true
        ) @RequestBody createRequest: CreateSkillDesiredRequest
    ): ResponseEntity<SkillDesiredDto> {
        return try {
            val savedSkillDesired = skillService.saveSkillDesired(createRequest)
            ResponseEntity.status(HttpStatus.CREATED).body(savedSkillDesired)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/desired/{id}")
    @Operation(
        summary = "Update skill desired",
        description = "Updates an existing skill desired"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill desired updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillDesiredDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill desired not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateSkillDesired(
        @Parameter(
            description = "Unique identifier of the skill desired to update",
            example = "1",
            required = true
        ) @PathVariable id: Long,
        @Parameter(
            description = "Updated skill desired data",
            required = true
        ) @RequestBody updateRequest: UpdateSkillDesiredRequest
    ): ResponseEntity<SkillDesiredDto> {
        return try {
            val updatedSkillDesired = skillService.updateSkillDesired(id, updateRequest)
            ResponseEntity.ok(updatedSkillDesired)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/desired/{id}")
    @Operation(
        summary = "Delete skill desired",
        description = "Deletes a skill desired from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Skill desired deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill desired not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSkillDesired(
        @Parameter(
            description = "Unique identifier of the skill desired to delete",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Void> {
        return try {
            skillService.deleteSkillDesiredById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/desired/user/{userUid}/skill/{skillId}")
    @Operation(
        summary = "Delete skill desired by user and skill",
        description = "Deletes a specific skill desired by a user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Skill desired deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User or skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSkillDesiredByUserAndSkill(
        @Parameter(
            description = "User UID",
            example = "user123",
            required = true
        ) @PathVariable userUid: String,
        @Parameter(
            description = "Skill ID",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<Void> {
        return try {
            skillService.deleteSkillDesiredByUserAndSkill(userUid, skillId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/desired/{id}/exists")
    @Operation(
        summary = "Check skill desired existence",
        description = "Checks if a skill desired exists by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Skill Desired Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkSkillDesiredExists(
        @Parameter(
            description = "Unique identifier of the skill desired to check",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsSkillDesiredById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/desired/user/{userUid}/skill/{skillId}/exists")
    @Operation(
        summary = "Check if user desires skill",
        description = "Checks if a user desires a specific skill"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "User Desires Skill",
                        value = "{\"exists\": false}"
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User or skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun checkUserDesiresSkill(
        @Parameter(
            description = "User UID",
            example = "user123",
            required = true
        ) @PathVariable userUid: String,
        @Parameter(
            description = "Skill ID",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        return try {
            val exists = skillService.existsSkillDesiredByUserAndSkill(userUid, skillId)
            ResponseEntity.ok(mapOf("exists" to exists))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/offered")
    @Operation(
        summary = "Get all skills offered",
        description = "Retrieves a list of all skills offered by users"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of skills offered",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillOfferedDto>::class)
                )]
            )
        ]
    )
    fun getAllSkillsOffered(): ResponseEntity<List<SkillOfferedDto>> {
        val skillsOffered = skillService.findAllSkillOffered()
        return ResponseEntity.ok(skillsOffered)
    }

    @GetMapping("/offered/{id}")
    @Operation(
        summary = "Get skill offered by ID",
        description = "Retrieves a specific skill offered by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill offered found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillOfferedDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill offered not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getSkillOfferedById(
        @Parameter(
            description = "Unique identifier of the skill offered to retrieve",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<SkillOfferedDto> {
        return skillService.findSkillOfferedById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/offered/user/{userUid}")
    @Operation(
        summary = "Get skills offered by user",
        description = "Retrieves all skills offered by a specific user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved skills offered by user",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillOfferedDto>::class)
                )]
            )
        ]
    )
    fun getSkillsOfferedByUser(
        @Parameter(
            description = "User UID to retrieve offered skills for",
            example = "user123",
            required = true
        ) @PathVariable userUid: String
    ): ResponseEntity<List<SkillOfferedDto>> {
        val skillsOffered = skillService.findSkillOfferedByUserUid(userUid)
        return ResponseEntity.ok(skillsOffered)
    }

    @GetMapping("/offered/skill/{skillId}")
    @Operation(
        summary = "Get users who offer a skill",
        description = "Retrieves all users who offer a specific skill"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users who offer the skill",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<SkillOfferedDto>::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getUsersWhoOfferSkill(
        @Parameter(
            description = "Skill ID to find users who offer it",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<List<SkillOfferedDto>> {
        return try {
            val skillsOffered = skillService.findSkillOfferedBySkillId(skillId)
            ResponseEntity.ok(skillsOffered)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/offered")
    @Operation(
        summary = "Create new skill offered",
        description = "Creates a new skill offered by a user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Skill offered created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillOfferedDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data or user/skill not found or already exists",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createSkillOffered(
        @Parameter(
            description = "Skill offered data for creation",
            required = true
        ) @RequestBody createRequest: CreateSkillOfferedRequest
    ): ResponseEntity<SkillOfferedDto> {
        return try {
            val savedSkillOffered = skillService.saveSkillOffered(createRequest)
            ResponseEntity.status(HttpStatus.CREATED).body(savedSkillOffered)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/offered/{id}")
    @Operation(
        summary = "Update skill offered",
        description = "Updates an existing skill offered"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Skill offered updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SkillOfferedDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill offered not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateSkillOffered(
        @Parameter(
            description = "Unique identifier of the skill offered to update",
            example = "1",
            required = true
        ) @PathVariable id: Long,
        @Parameter(
            description = "Updated skill offered data",
            required = true
        ) @RequestBody updateRequest: UpdateSkillOfferedRequest
    ): ResponseEntity<SkillOfferedDto> {
        return try {
            val updatedSkillOffered = skillService.updateSkillOffered(id, updateRequest)
            ResponseEntity.ok(updatedSkillOffered)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/offered/{id}")
    @Operation(
        summary = "Delete skill offered",
        description = "Deletes a skill offered from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Skill offered deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Skill offered not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSkillOffered(
        @Parameter(
            description = "Unique identifier of the skill offered to delete",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Void> {
        return try {
            skillService.deleteSkillOfferedById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/offered/user/{userUid}/skill/{skillId}")
    @Operation(
        summary = "Delete skill offered by user and skill",
        description = "Deletes a specific skill offered by a user"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Skill offered deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User or skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteSkillOfferedByUserAndSkill(
        @Parameter(
            description = "User UID",
            example = "user123",
            required = true
        ) @PathVariable userUid: String,
        @Parameter(
            description = "Skill ID",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<Void> {
        return try {
            skillService.deleteSkillOfferedByUserAndSkill(userUid, skillId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/offered/{id}/exists")
    @Operation(
        summary = "Check skill offered existence",
        description = "Checks if a skill offered exists by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Skill Offered Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkSkillOfferedExists(
        @Parameter(
            description = "Unique identifier of the skill offered to check",
            example = "1",
            required = true
        ) @PathVariable id: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsSkillOfferedById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/offered/user/{userUid}/skill/{skillId}/exists")
    @Operation(
        summary = "Check if user offers skill",
        description = "Checks if a user offers a specific skill"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "User Offers Skill",
                        value = "{\"exists\": false}"
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User or skill not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun checkUserOffersSkill(
        @Parameter(
            description = "User UID",
            example = "user123",
            required = true
        ) @PathVariable userUid: String,
        @Parameter(
            description = "Skill ID",
            example = "1",
            required = true
        ) @PathVariable skillId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        return try {
            val exists = skillService.existsSkillOfferedByUserAndSkill(userUid, skillId)
            ResponseEntity.ok(mapOf("exists" to exists))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}