package govi.feature.products.data.repository

import govi.core.network.NetworkResult
import govi.core.network.safeApiCall
import govi.feature.products.data.remote.ProductsApiService
import govi.feature.products.domain.model.ProductsItem
import govi.feature.products.domain.repository.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ProductsApiService
) : ProductsRepository {

    override suspend fun getItems(): Result<List<ProductsItem>> {
        return when (val result = safeApiCall { apiService.getItems() }) {
            is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    override suspend fun getItemById(id: String): Result<ProductsItem> {
        return when (val result = safeApiCall { apiService.getItemById(id) }) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }
}
