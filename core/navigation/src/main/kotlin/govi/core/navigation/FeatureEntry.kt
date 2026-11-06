package govi.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface FeatureEntry {
    val featureRoute: String

    fun registerRoute(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    )
}
