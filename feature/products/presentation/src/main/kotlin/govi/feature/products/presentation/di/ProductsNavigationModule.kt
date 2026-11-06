package govi.feature.products.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import govi.core.navigation.FeatureEntry
import govi.feature.products.presentation.navigation.ProductsFeatureEntryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindProductsFeatureEntry(
        entry: ProductsFeatureEntryImpl
    ): FeatureEntry
}
