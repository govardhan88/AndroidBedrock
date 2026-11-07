package govi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import govi.core.navigation.AppNavigator
import govi.core.navigation.FeatureEntry
import govi.core.navigation.NavigationIntent
import govi.core.navigation.route.AppRoute
import govi.core.ui.theme.AppTheme
import govi.core.ui.util.ObserveAsEvents
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    ObserveAsEvents(appNavigator.navigationChannel) { intent ->
                        when (intent) {
                            is NavigationIntent.NavigateTo -> {
                                val route = intent.route
                                val popUp = intent.popUpToRoute
                                when (route) {
                                    is String -> {
                                        navController.navigate(route) {
                                            popUp?.let { target ->
                                                if (target is String) {
                                                    popUpTo(target) {
                                                        inclusive = intent.inclusive
                                                    }
                                                } else {
                                                    popUpTo(target) {
                                                        inclusive = intent.inclusive
                                                    }
                                                }
                                            }
                                            launchSingleTop = intent.isSingleTop
                                        }
                                    }
                                    else -> {
                                        navController.navigate(route) {
                                            popUp?.let { target ->
                                                if (target is String) {
                                                    popUpTo(target) {
                                                        inclusive = intent.inclusive
                                                    }
                                                } else {
                                                    popUpTo(target) {
                                                        inclusive = intent.inclusive
                                                    }
                                                }
                                            }
                                            launchSingleTop = intent.isSingleTop
                                        }
                                    }
                                }
                            }
                            is NavigationIntent.NavigateBack -> {
                                navController.popBackStack()
                            }
                            is NavigationIntent.PopTo -> {
                                when (val route = intent.route) {
                                    is String -> navController.popBackStack(route, intent.inclusive)
                                    else -> navController.popBackStack(route, intent.inclusive)
                                }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = AppRoute.Users,
                    ) {
                        featureEntries.forEach { entry ->
                            entry.registerRoute(this, navController)
                        }
                    }
                }
            }
        }
    }
}
