package com.govi.androidbedrock.feature.demo.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.govi.androidbedrock.core.navigation.FeatureEntry
import com.govi.androidbedrock.core.navigation.route.AppRoute
import com.govi.androidbedrock.feature.demo.presentation.DemoRoute
import com.govi.androidbedrock.feature.demo.presentation.detail.DemoDetailRoute
import javax.inject.Inject

class DemoFeatureEntryImpl @Inject constructor() : FeatureEntry {
    override fun registerRoute(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<AppRoute.Demo> {
            DemoRoute()
        }
        navGraphBuilder.composable<AppRoute.DemoDetail> {
            DemoDetailRoute()
        }
    }
}
