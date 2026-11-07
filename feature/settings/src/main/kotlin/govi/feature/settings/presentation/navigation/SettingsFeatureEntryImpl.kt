package govi.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.core.navigation.route.AppRoute
import govi.feature.settings.presentation.SettingsRoute
import javax.inject.Inject

class SettingsFeatureEntryImpl
    @Inject
    constructor() : FeatureEntry {
        override fun registerRoute(
            navGraphBuilder: NavGraphBuilder,
            navController: NavHostController,
        ) {
            navGraphBuilder.composable<AppRoute.Settings> {
                SettingsRoute()
            }
        }
    }
