package govi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import govi.core.navigation.AppNavigator
import govi.core.navigation.FeatureEntry
import govi.core.navigation.NavigationIntent
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
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    LaunchedEffect(appNavigator) {
                        appNavigator.navigationChannel.collect { intent ->
                            when (intent) {
                                is NavigationIntent.NavigateTo -> {
                                    navController.navigate(intent.route) {
                                        intent.popUpToRoute?.let { popUpRoute ->
                                            popUpTo(popUpRoute) {
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
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "users"
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
