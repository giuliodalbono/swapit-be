package io.github.giuliodalbono.swapit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.*
import java.util.Base64

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var userService: UserService

    private val testUid = "test-uid"
    private val testEmail = "test@example.com"
    private val testUsername = "testUser"
    private val testProfilePicture = "test-profile-picture"
    private val testDateTime = LocalDateTime.now()

    private val testUserDto = UserDto(
        uid = testUid,
        email = testEmail,
        username = testUsername,
        profilePicture = testProfilePicture.toByteArray(),
        skillDesired = setOf(),
        skillOffered = setOf(),
        version = 0L,
        creationTime = testDateTime,
        lastUpdate = testDateTime
    )

    private val createUserRequest = CreateUserRequest(
        uid = testUid,
        email = testEmail,
        username = testUsername,
        profilePicture = testProfilePicture,
    )

    private val updateUserRequest = UpdateUserRequest(
        email = "updated@example.com",
        username = "updatedUser",
        profilePicture = testProfilePicture,
    )

    @Test
    fun `getAllUsers should return all users`() {
        // Given
        whenever(userService.findAll()).thenReturn(listOf(testUserDto))

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].uid").value(testUid))
            .andExpect(jsonPath("$[0].email").value(testEmail))
            .andExpect(jsonPath("$[0].username").value(testUsername))
            .andExpect(jsonPath("$[0].profilePicture").value(Base64.getEncoder().encodeToString(testProfilePicture.toByteArray())))
    }

    @Test
    fun `getUserByUid should return user when found`() {
        whenever(userService.findByUid(testUid)).thenReturn(Optional.of(testUserDto))

        mockMvc.perform(get("/api/users/{uid}", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(Base64.getEncoder().encodeToString(testProfilePicture.toByteArray())))
    }

    @Test
    fun `getUserByUid should return 404 when not found`() {
        whenever(userService.findByUid(testUid)).thenReturn(Optional.empty())

        mockMvc.perform(get("/api/users/{uid}", testUid))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getUserByEmail should return user when found`() {
        whenever(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUserDto))

        mockMvc.perform(get("/api/users/email/{email}", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(Base64.getEncoder().encodeToString(testProfilePicture.toByteArray())))
    }

    @Test
    fun `getUserByEmail should return 404 when not found`() {
        whenever(userService.findByEmail(testEmail)).thenReturn(Optional.empty())

        mockMvc.perform(get("/api/users/email/{email}", testEmail))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createUser should create and return user`() {
        whenever(userService.save(any())).thenReturn(testUserDto)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(Base64.getEncoder().encodeToString(testProfilePicture.toByteArray())))
    }

    @Test
    fun `updateUser should update and return user when found`() {
        val updatedUserDto = testUserDto.copy(email = "updated@example.com", username = "updatedUser")
        whenever(userService.update(any(), any())).thenReturn(updatedUserDto)

        mockMvc.perform(
            put("/api/users/{uid}", testUid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.username").value("updatedUser"))
            .andExpect(jsonPath("$.profilePicture").value(Base64.getEncoder().encodeToString(testProfilePicture.toByteArray())))
    }

    @Test
    fun `updateUser should return 404 when user not found`() {
        whenever(userService.update(any(), any()))
            .thenThrow(IllegalArgumentException("User not found"))

        mockMvc.perform(
            put("/api/users/{uid}", testUid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteUser should delete user when exists`() {
        doNothing().whenever(userService).deleteById(testUid)

        mockMvc.perform(delete("/api/users/{uid}", testUid))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser should return 404 when user not found`() {
        doThrow(IllegalArgumentException("User not found")).whenever(userService).deleteById(testUid)

        mockMvc.perform(delete("/api/users/{uid}", testUid))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `checkUserExists should return true when user exists`() {
        whenever(userService.existsById(testUid)).thenReturn(true)

        mockMvc.perform(get("/api/users/{uid}/exists", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `checkUserExists should return false when user does not exist`() {
        whenever(userService.existsById(testUid)).thenReturn(false)

        mockMvc.perform(get("/api/users/{uid}/exists", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))
    }

    @Test
    fun `checkEmailExists should return true when email exists`() {
        whenever(userService.existsByEmail(testEmail)).thenReturn(true)

        mockMvc.perform(get("/api/users/email/{email}/exists", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `checkEmailExists should return false when email does not exist`() {
        whenever(userService.existsByEmail(testEmail)).thenReturn(false)

        mockMvc.perform(get("/api/users/email/{email}/exists", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))
    }
}
