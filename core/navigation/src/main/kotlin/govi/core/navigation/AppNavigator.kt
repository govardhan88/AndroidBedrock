package govi.core.navigation

import kotlinx.coroutines.flow.Flow

interface AppNavigator {
    val navigationChannel: Flow<NavigationIntent>

    fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = true
    )

    fun navigateBack()

    fun popTo(
        route: String,
        inclusive: Boolean = false
    )
}
