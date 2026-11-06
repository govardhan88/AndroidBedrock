package govi.feature.products.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.feature.products.presentation.ProductsScreen
import javax.inject.Inject

class ProductsFeatureEntryImpl @Inject constructor() : FeatureEntry {
    override val featureRoute: String = "products"

    override fun registerRoute(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(featureRoute) {
            ProductsScreen()
        }
    }
}
