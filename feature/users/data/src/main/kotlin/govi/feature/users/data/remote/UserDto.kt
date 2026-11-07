package govi.feature.users.data.remote

import govi.feature.users.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("avatar") val avatar: String? = null,
) {
    fun toDomain(): User =
        User(
            id = id,
            name = name,
            email = email,
            avatarUrl = avatar.orEmpty(),
        )
}
