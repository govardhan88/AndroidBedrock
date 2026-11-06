package govi.feature.products.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.feature.products.data.remote.ProductsApiService
import govi.feature.products.data.repository.ProductsRepositoryImpl
import govi.feature.products.domain.repository.ProductsRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsDataModule {

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository

    companion object {
        @Provides
        @Singleton
        fun provideProductsApiService(retrofit: Retrofit): ProductsApiService {
            return retrofit.create(ProductsApiService::class.java)
        }
    }
}
