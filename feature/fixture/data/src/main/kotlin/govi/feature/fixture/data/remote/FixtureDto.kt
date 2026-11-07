package govi.feature.fixture.data.remote

import govi.feature.fixture.domain.model.FixtureItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FixtureDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
) {
    fun toDomain(): FixtureItem =
        FixtureItem(
            id = id,
            title = title,
            description = description.orEmpty(),
        )
}
