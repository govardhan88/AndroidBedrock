package govi.feature.products.domain.repository

import govi.feature.products.domain.model.ProductsItem

interface ProductsRepository {
    suspend fun getItems(): Result<List<ProductsItem>>

    suspend fun getItemById(id: String): Result<ProductsItem>
}
