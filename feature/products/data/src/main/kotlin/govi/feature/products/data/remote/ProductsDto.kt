package govi.feature.products.data.remote

import govi.feature.products.domain.model.ProductsItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null
) {
    fun toDomain(): ProductsItem = ProductsItem(
        id = id,
        title = title,
        description = description.orEmpty()
    )
}
