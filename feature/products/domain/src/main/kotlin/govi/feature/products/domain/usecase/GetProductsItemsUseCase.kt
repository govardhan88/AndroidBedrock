package govi.feature.products.domain.usecase

import govi.feature.products.domain.model.ProductsItem
import govi.feature.products.domain.repository.ProductsRepository
import javax.inject.Inject

class GetProductsItemsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(): Result<List<ProductsItem>> {
        return repository.getItems()
    }
}
