package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.mapper.UserMapper
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.model.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    fun findAll(): List<UserDto> = userRepository.findAll().map { userMapper.toDto(it) }

    fun findByUid(uid: String): Optional<UserDto> = userRepository.findById(uid).map { userMapper.toDto(it) }

    fun findByEmail(email: String): Optional<UserDto> = userRepository.findByEmail(email).map { userMapper.toDto(it) }

    fun save(createRequest: CreateUserRequest): UserDto {
        val user = userMapper.toEntity(createRequest)
        val savedUser = userRepository.save(user)
        return userMapper.toDto(savedUser)
    }

    fun update(uid: String, updateRequest: UpdateUserRequest): UserDto {
        val existingUser = userRepository.findById(uid)
            .orElseThrow { IllegalArgumentException("User with uid $uid not found") }
        
        val updatedUser = userMapper.updateEntity(existingUser, updateRequest)
        val savedUser = userRepository.save(updatedUser)
        return userMapper.toDto(savedUser)
    }

    fun deleteById(uid: String) {
        if (!userRepository.existsById(uid)) {
            throw IllegalArgumentException("User with uid $uid not found")
        }
        userRepository.deleteById(uid)
    }

    fun existsById(uid: String): Boolean = userRepository.existsById(uid)

    fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    fun findEntityByUid(uid: String): Optional<User> = userRepository.findById(uid)
}