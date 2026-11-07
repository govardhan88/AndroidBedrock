package govi.core.navigation.route

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
    data object Users : AppRoute

    @Serializable
    data object Products : AppRoute

    @Serializable
    data class ProductDetail(
        val productId: String,
    ) : AppRoute

    @Serializable
    data object Fixture : AppRoute

    @Serializable
    data object Settings : AppRoute
}
