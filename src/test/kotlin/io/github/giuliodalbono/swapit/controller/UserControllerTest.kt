package io.github.giuliodalbono.swapit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.service.UserService
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
    private val testUsername = "testuser"
    private val testProfilePicture = byteArrayOf(10, 20, 30, 40, 50)
    private val testDateTime = LocalDateTime.now()

    private val testUserDto = UserDto(
        uid = testUid,
        email = testEmail,
        username = testUsername,
        profilePicture = testProfilePicture,
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
        username = "updateduser",
        profilePicture = testProfilePicture,
    )

    @Test
    fun `getAllUsers should return all users`() {
        // Given
        val users = listOf(testUserDto)
        `when`(userService.findAll()).thenReturn(users)

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].uid").value(testUid))
            .andExpect(jsonPath("$[0].email").value(testEmail))
            .andExpect(jsonPath("$[0].username").value(testUsername))
            .andExpect(jsonPath("$[0].profilePicture").value(testProfilePicture))
    }

    @Test
    fun `getUserByUid should return user when found`() {
        // Given
        `when`(userService.findByUid(testUid)).thenReturn(Optional.of(testUserDto))

        // When & Then
        mockMvc.perform(get("/api/users/{uid}", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(testProfilePicture))
    }

    @Test
    fun `getUserByUid should return 404 when not found`() {
        // Given
        `when`(userService.findByUid(testUid)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/users/{uid}", testUid))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getUserByEmail should return user when found`() {
        // Given
        `when`(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUserDto))

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(testProfilePicture))
    }

    @Test
    fun `getUserByEmail should return 404 when not found`() {
        // Given
        `when`(userService.findByEmail(testEmail)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", testEmail))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createUser should create and return user`() {
        // Given
        `when`(userService.save(createUserRequest)).thenReturn(testUserDto)

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.username").value(testUsername))
            .andExpect(jsonPath("$.profilePicture").value(testProfilePicture))
    }

    @Test
    fun `updateUser should update and return user when found`() {
        // Given
        val updatedUserDto = testUserDto.copy(email = "updated@example.com", username = "updateduser")
        `when`(userService.update(testUid, updateUserRequest)).thenReturn(updatedUserDto)

        // When & Then
        mockMvc.perform(put("/api/users/{uid}", testUid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uid").value(testUid))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.profilePicture").value(testProfilePicture))
    }

    @Test
    fun `updateUser should return 404 when user not found`() {
        // Given
        `when`(userService.update(testUid, updateUserRequest)).thenThrow(IllegalArgumentException("User not found"))

        // When & Then
        mockMvc.perform(put("/api/users/{uid}", testUid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteUser should delete user when exists`() {
        // Given
        doNothing().`when`(userService).deleteById(testUid)

        // When & Then
        mockMvc.perform(delete("/api/users/{uid}", testUid))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser should return 404 when user not found`() {
        // Given
        doThrow(IllegalArgumentException("User not found")).`when`(userService).deleteById(testUid)

        // When & Then
        mockMvc.perform(delete("/api/users/{uid}", testUid))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `checkUserExists should return true when user exists`() {
        // Given
        `when`(userService.existsById(testUid)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/users/{uid}/exists", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `checkUserExists should return false when user does not exist`() {
        // Given
        `when`(userService.existsById(testUid)).thenReturn(false)

        // When & Then
        mockMvc.perform(get("/api/users/{uid}/exists", testUid))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))
    }

    @Test
    fun `checkEmailExists should return true when email exists`() {
        // Given
        `when`(userService.existsByEmail(testEmail)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}/exists", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `checkEmailExists should return false when email does not exist`() {
        // Given
        `when`(userService.existsByEmail(testEmail)).thenReturn(false)

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}/exists", testEmail))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))
    }
}
