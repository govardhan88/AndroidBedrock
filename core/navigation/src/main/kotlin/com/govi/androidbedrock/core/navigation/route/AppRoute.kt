package com.govi.androidbedrock.core.navigation.route

import kotlinx.serialization.Serializable

/**
 * Type-Safe Navigation Contract for application destinations.
 *
 * All feature modules register their Composable screens using these typed destinations.
 * Cross-feature navigation is completely verified at compile-time with zero raw strings.
 */
@Serializable
sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute
}
