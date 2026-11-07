package govi.feature.settings.data.remote

import govi.feature.settings.domain.model.SettingsItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
) {
    fun toDomain(): SettingsItem =
        SettingsItem(
            id = id,
            title = title,
            description = description.orEmpty(),
        )
}
