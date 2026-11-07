package govi.feature.fixture.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import govi.core.navigation.FeatureEntry
import govi.feature.fixture.presentation.FixtureScreen
import javax.inject.Inject

class FixtureFeatureEntryImpl
    @Inject
    constructor() : FeatureEntry {
        override val featureRoute: String = "fixture"

        override fun registerRoute(
            navGraphBuilder: NavGraphBuilder,
            navController: NavHostController,
        ) {
            navGraphBuilder.composable(featureRoute) {
                FixtureScreen()
            }
        }
    }
