package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateSkillRequest
import io.github.giuliodalbono.swapit.dto.SkillDto
import io.github.giuliodalbono.swapit.dto.UpdateSkillRequest
import io.github.giuliodalbono.swapit.service.SkillService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/skills")
class SkillController(private val skillService: SkillService) {

    @GetMapping
    fun getAllSkills(): ResponseEntity<List<SkillDto>> {
        val skills = skillService.findAll()
        return ResponseEntity.ok(skills)
    }

    @GetMapping("/{id}")
    fun getSkillById(@PathVariable id: Long): ResponseEntity<SkillDto> {
        return skillService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/label/{label}")
    fun getSkillByLabel(@PathVariable label: String): ResponseEntity<SkillDto> {
        return skillService.findByLabel(label)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    fun createSkill(@RequestBody createRequest: CreateSkillRequest): ResponseEntity<SkillDto> {
        val savedSkill = skillService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill)
    }

    @PutMapping("/{id}")
    fun updateSkill(@PathVariable id: Long, @RequestBody updateRequest: UpdateSkillRequest): ResponseEntity<SkillDto> {
        return try {
            val updatedSkill = skillService.update(id, updateRequest)
            ResponseEntity.ok(updatedSkill)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSkill(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            skillService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/exists")
    fun checkSkillExists(@PathVariable id: Long): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/label/{label}/exists")
    fun checkLabelExists(@PathVariable label: String): ResponseEntity<Map<String, Boolean>> {
        val exists = skillService.existsByLabel(label)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}