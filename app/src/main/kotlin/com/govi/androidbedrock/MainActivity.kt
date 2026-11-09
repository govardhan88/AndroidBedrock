package com.govi.androidbedrock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.FeatureEntry
import com.govi.androidbedrock.core.navigation.NavigationIntent
import com.govi.androidbedrock.core.navigation.route.AppRoute
import com.govi.androidbedrock.core.network.connectivity.NetworkMonitor
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetFormat
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetHost
import com.govi.androidbedrock.core.ui.theme.AppTheme
import com.govi.androidbedrock.core.ui.util.ObserveAsEvents
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(
                        initialValue = networkMonitor.isCurrentlyOnline,
                    )

                    ObserveAsEvents(appNavigator.navigationChannel) { intent ->
                        when (intent) {
                            is NavigationIntent.NavigateTo -> {
                                navController.navigate(intent.route) {
                                    intent.popUpToRoute?.let { target ->
                                        popUpTo(target) {
                                            inclusive = intent.inclusive
                                        }
                                    }
                                    launchSingleTop = intent.isSingleTop
                                }
                            }
                            is NavigationIntent.NavigateBack -> {
                                navController.popBackStack()
                            }
                            is NavigationIntent.PopTo -> {
                                navController.popBackStack(intent.route, intent.inclusive)
                            }
                        }
                    }

                    NoInternetHost(
                        isOffline = !isOnline,
                        format = NoInternetFormat.Dialog,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = AppRoute.Home,
                        ) {
                            composable<AppRoute.Home> {
                                StarterScreen()
                            }
                            featureEntries.forEach { entry ->
                                entry.registerRoute(this, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
