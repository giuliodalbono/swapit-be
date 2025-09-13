package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.model.entity.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toDto(user: User): UserDto {
        return UserDto(
            uid = user.uid!!,
            email = user.email!!,
            username = user.username!!,
            version = user.version,
            creationTime = user.creationTime!!,
            lastUpdate = user.lastUpdate!!
        )
    }

    fun toEntity(createRequest: CreateUserRequest): User {
        return User().apply {
            uid = createRequest.uid
            email = createRequest.email
            username = createRequest.username
        }
    }

    fun updateEntity(user: User, updateRequest: UpdateUserRequest): User {
        user.email = updateRequest.email
        user.username = updateRequest.username
        return user
    }
}
