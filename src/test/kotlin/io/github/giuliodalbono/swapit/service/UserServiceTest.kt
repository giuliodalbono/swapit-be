package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.mapper.UserMapper
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.repository.UserRepository
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
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userMapper: UserMapper

    @InjectMocks
    private lateinit var userService: UserService

    private val testUid = "test-uid"
    private val testEmail = "test@example.com"
    private val testUsername = "testuser"
    private val testProfilePicture = "test-profile-picture"
    private val testDateTime = LocalDateTime.now()

    private val testUser = User().apply {
        uid = testUid
        email = testEmail
        username = testUsername
        profilePicture = testProfilePicture.toByteArray()
        version = 0L
        creationTime = testDateTime
        lastUpdate = testDateTime
    }

    private val testUserDto = UserDto(
        uid = testUid,
        email = testEmail,
        username = testUsername,
        profilePicture = testProfilePicture.toByteArray(),
        version = 0L,
        creationTime = testDateTime,
        skillDesired = setOf(),
        skillOffered = setOf(),
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
    fun `findAll should return all users`() {
        // Given
        val users = listOf(testUser)
        val userDtos = listOf(testUserDto)
        `when`(userRepository.findAll()).thenReturn(users)
        `when`(userMapper.toDto(testUser)).thenReturn(testUserDto)

        // When
        val result = userService.findAll()

        // Then
        assert(result == userDtos)
        verify(userRepository).findAll()
        verify(userMapper).toDto(testUser)
    }

    @Test
    fun `findByUid should return user when found`() {
        // Given
        `when`(userRepository.findById(testUid)).thenReturn(Optional.of(testUser))
        `when`(userMapper.toDto(testUser)).thenReturn(testUserDto)

        // When
        val result = userService.findByUid(testUid)

        // Then
        assert(result.isPresent)
        assert(result.get() == testUserDto)
        verify(userRepository).findById(testUid)
        verify(userMapper).toDto(testUser)
    }

    @Test
    fun `findByUid should return empty when not found`() {
        // Given
        `when`(userRepository.findById(testUid)).thenReturn(Optional.empty())

        // When
        val result = userService.findByUid(testUid)

        // Then
        assert(!result.isPresent)
        verify(userRepository).findById(testUid)
        verify(userMapper, never()).toDto(testUser)
    }

    @Test
    fun `findByEmail should return user when found`() {
        // Given
        `when`(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser))
        `when`(userMapper.toDto(testUser)).thenReturn(testUserDto)

        // When
        val result = userService.findByEmail(testEmail)

        // Then
        assert(result.isPresent)
        assert(result.get() == testUserDto)
        verify(userRepository).findByEmail(testEmail)
        verify(userMapper).toDto(testUser)
    }

    @Test
    fun `save should create and return user`() {
        // Given
        `when`(userMapper.toEntity(createUserRequest)).thenReturn(testUser)
        `when`(userRepository.save(testUser)).thenReturn(testUser)
        `when`(userMapper.toDto(testUser)).thenReturn(testUserDto)

        // When
        val result = userService.save(createUserRequest)

        // Then
        assert(result == testUserDto)
        verify(userMapper).toEntity(createUserRequest)
        verify(userRepository).save(testUser)
        verify(userMapper).toDto(testUser)
    }

    @Test
    fun `update should update and return user when found`() {
        // Given
        val updatedUserDto = testUserDto.copy(email = "updated@example.com", username = "updateduser")
        `when`(userRepository.findById(testUid)).thenReturn(Optional.of(testUser))
        `when`(userMapper.updateEntity(testUser, updateUserRequest)).thenReturn(testUser)
        `when`(userRepository.save(testUser)).thenReturn(testUser)
        `when`(userMapper.toDto(testUser)).thenReturn(updatedUserDto)

        // When
        val result = userService.update(testUid, updateUserRequest)

        // Then
        assert(result == updatedUserDto)
        verify(userRepository).findById(testUid)
        verify(userMapper).updateEntity(testUser, updateUserRequest)
        verify(userRepository).save(testUser)
        verify(userMapper).toDto(testUser)
    }

    @Test
    fun `update should throw exception when user not found`() {
        // Given
        `when`(userRepository.findById(testUid)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            userService.update(testUid, updateUserRequest)
        }
        verify(userRepository).findById(testUid)
        verify(userMapper, never()).updateEntity(testUser, updateUserRequest)
        verify(userRepository, never()).save(testUser)
    }

    @Test
    fun `deleteById should delete user when exists`() {
        // Given
        `when`(userRepository.existsById(testUid)).thenReturn(true)

        // When
        userService.deleteById(testUid)

        // Then
        verify(userRepository).existsById(testUid)
        verify(userRepository).deleteById(testUid)
    }

    @Test
    fun `deleteById should throw exception when user not found`() {
        // Given
        `when`(userRepository.existsById(testUid)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            userService.deleteById(testUid)
        }
        verify(userRepository).existsById(testUid)
        verify(userRepository, never()).deleteById(any())
    }

    @Test
    fun `existsById should return true when user exists`() {
        // Given
        `when`(userRepository.existsById(testUid)).thenReturn(true)

        // When
        val result = userService.existsById(testUid)

        // Then
        assert(result)
        verify(userRepository).existsById(testUid)
    }

    @Test
    fun `existsById should return false when user does not exist`() {
        // Given
        `when`(userRepository.existsById(testUid)).thenReturn(false)

        // When
        val result = userService.existsById(testUid)

        // Then
        assert(!result)
        verify(userRepository).existsById(testUid)
    }

    @Test
    fun `existsByEmail should return true when email exists`() {
        // Given
        `when`(userRepository.existsByEmail(testEmail)).thenReturn(true)

        // When
        val result = userService.existsByEmail(testEmail)

        // Then
        assert(result)
        verify(userRepository).existsByEmail(testEmail)
    }

    @Test
    fun `existsByEmail should return false when email does not exist`() {
        // Given
        `when`(userRepository.existsByEmail(testEmail)).thenReturn(false)

        // When
        val result = userService.existsByEmail(testEmail)

        // Then
        assert(!result)
        verify(userRepository).existsByEmail(testEmail)
    }
}
