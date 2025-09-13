package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.findAll()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{uid}")
    fun getUserByUid(@PathVariable uid: String): ResponseEntity<UserDto> {
        return userService.findByUid(uid)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserDto> {
        return userService.findByEmail(email)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    fun createUser(@RequestBody createRequest: CreateUserRequest): ResponseEntity<UserDto> {
        val savedUser = userService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @PutMapping("/{uid}")
    fun updateUser(@PathVariable uid: String, @RequestBody updateRequest: UpdateUserRequest): ResponseEntity<UserDto> {
        return try {
            val updatedUser = userService.update(uid, updateRequest)
            ResponseEntity.ok(updatedUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{uid}")
    fun deleteUser(@PathVariable uid: String): ResponseEntity<Void> {
        return try {
            userService.deleteById(uid)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{uid}/exists")
    fun checkUserExists(@PathVariable uid: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsById(uid)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/email/{email}/exists")
    fun checkEmailExists(@PathVariable email: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsByEmail(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}