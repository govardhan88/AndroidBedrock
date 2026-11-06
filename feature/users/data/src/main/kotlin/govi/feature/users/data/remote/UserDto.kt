package govi.feature.users.data.remote

import com.google.gson.annotations.SerializedName
import govi.feature.users.domain.model.User

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatar") val avatar: String? = null
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatar.orEmpty()
    )
}
