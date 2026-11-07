package govi.feature.products.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.core.navigation.route.AppRoute
import govi.feature.products.presentation.ProductsRoute
import javax.inject.Inject

class ProductsFeatureEntryImpl
    @Inject
    constructor() : FeatureEntry {
        override fun registerRoute(
            navGraphBuilder: NavGraphBuilder,
            navController: NavHostController,
        ) {
            navGraphBuilder.composable<AppRoute.Products> {
                ProductsRoute()
            }
        }
    }
