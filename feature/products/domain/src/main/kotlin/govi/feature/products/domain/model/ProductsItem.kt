package govi.feature.products.domain.model

data class ProductsItem(
    val id: String,
    val title: String,
    val description: String = "",
)
