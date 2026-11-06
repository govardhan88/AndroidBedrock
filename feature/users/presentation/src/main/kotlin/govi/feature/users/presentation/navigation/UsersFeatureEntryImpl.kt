package govi.feature.users.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.feature.users.presentation.UsersScreen
import javax.inject.Inject

class UsersFeatureEntryImpl @Inject constructor() : FeatureEntry {

    override val featureRoute: String = "users"

    override fun registerRoute(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(featureRoute) {
            UsersScreen()
        }
    }
}
