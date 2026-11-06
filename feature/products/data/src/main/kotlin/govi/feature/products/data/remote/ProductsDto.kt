package govi.feature.products.data.remote

import com.google.gson.annotations.SerializedName
import govi.feature.products.domain.model.ProductsItem

data class ProductsDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null
) {
    fun toDomain(): ProductsItem = ProductsItem(
        id = id,
        title = title,
        description = description.orEmpty()
    )
}
