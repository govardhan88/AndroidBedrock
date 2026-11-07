package govi.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.feature.settings.presentation.SettingsScreen
import javax.inject.Inject

class SettingsFeatureEntryImpl
    @Inject
    constructor() : FeatureEntry {
        override val featureRoute: String = "settings"

        override fun registerRoute(
            navGraphBuilder: NavGraphBuilder,
            navController: NavHostController,
        ) {
            navGraphBuilder.composable(featureRoute) {
                SettingsScreen()
            }
        }
    }
