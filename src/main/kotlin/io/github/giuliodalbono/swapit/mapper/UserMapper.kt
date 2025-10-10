package io.github.giuliodalbono.swapit.mapper

import io.github.giuliodalbono.swapit.dto.CreateUserRequest
import io.github.giuliodalbono.swapit.dto.UpdateUserRequest
import io.github.giuliodalbono.swapit.dto.UserDto
import io.github.giuliodalbono.swapit.model.entity.User
import io.github.giuliodalbono.swapit.service.SkillService
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val skillService: SkillService
) {

    fun toDto(user: User): UserDto {
        val desired = skillService.findSkillDesiredByUserUid(user.uid!!).map { it.skill.label }.toSet()
        val offered = skillService.findSkillOfferedByUserUid(user.uid!!).map { it.skill.label }.toSet()
        return UserDto(
            uid = user.uid!!,
            email = user.email!!,
            username = user.username!!,
            profilePicture = user.profilePicture,
            skillDesired = desired,
            skillOffered = offered,
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
            profilePicture = createRequest.profilePicture?.toByteArray()
        }
    }

    fun updateEntity(user: User, updateRequest: UpdateUserRequest): User {
        user.email = updateRequest.email
        user.username = updateRequest.username
        user.profilePicture = updateRequest.profilePicture?.toByteArray()
        return user
    }
}
