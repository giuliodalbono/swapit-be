package io.github.giuliodalbono.swapit.controller

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.service.UserService
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
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing user accounts and profiles")
class UserController(private val userService: UserService) {

    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all registered users in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of users",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<UserDto>::class)
                )]
            )
        ]
    )
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.findAll()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{uid}")
    @Operation(
        summary = "Get user by UID",
        description = "Retrieves a specific user by their unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getUserByUid(
        @Parameter(
            description = "Unique identifier of the user to retrieve",
            example = "user_12345",
            required = true
        ) @PathVariable uid: String
    ): ResponseEntity<UserDto> {
        return userService.findByUid(uid)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get user by email",
        description = "Retrieves a specific user by their email address"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getUserByEmail(
        @Parameter(
            description = "Email address of the user to retrieve",
            example = "john.doe@example.com",
            required = true
        ) @PathVariable email: String
    ): ResponseEntity<UserDto> {
        return userService.findByEmail(email)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    @Operation(
        summary = "Create new user",
        description = "Creates a new user account in the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createUser(
        @Parameter(
            description = "User data for account creation",
            required = true
        ) @RequestBody createRequest: CreateUserRequest
    ): ResponseEntity<UserDto> {
        val savedUser = userService.save(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @PutMapping("/{uid}")
    @Operation(
        summary = "Update user",
        description = "Updates an existing user's information"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updateUser(
        @Parameter(
            description = "Unique identifier of the user to update",
            example = "user_12345",
            required = true
        ) @PathVariable uid: String,
        @Parameter(
            description = "Updated user data",
            required = true
        ) @RequestBody updateRequest: UpdateUserRequest
    ): ResponseEntity<UserDto> {
        return try {
            val updatedUser = userService.update(uid, updateRequest)
            ResponseEntity.ok(updatedUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{uid}")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user account from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "User deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deleteUser(
        @Parameter(
            description = "Unique identifier of the user to delete",
            example = "user_12345",
            required = true
        ) @PathVariable uid: String
    ): ResponseEntity<Void> {
        return try {
            userService.deleteById(uid)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{uid}/exists")
    @Operation(
        summary = "Check user existence",
        description = "Checks if a user exists by their UID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "User Exists",
                        value = "{\"exists\": true}"
                    )]
                )]
            )
        ]
    )
    fun checkUserExists(
        @Parameter(
            description = "Unique identifier of the user to check",
            example = "user_12345",
            required = true
        ) @PathVariable uid: String
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsById(uid)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/email/{email}/exists")
    @Operation(
        summary = "Check email existence",
        description = "Checks if an email address is already registered"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Email existence check completed",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Email Exists",
                        value = "{\"exists\": false}"
                    )]
                )]
            )
        ]
    )
    fun checkEmailExists(
        @Parameter(
            description = "Email address to check",
            example = "john.doe@example.com",
            required = true
        ) @PathVariable email: String
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.existsByEmail(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}