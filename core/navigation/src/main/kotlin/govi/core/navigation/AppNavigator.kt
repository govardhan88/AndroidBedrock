package govi.core.navigation

import kotlinx.coroutines.flow.Flow

interface AppNavigator {
    val navigationChannel: Flow<NavigationIntent>

    fun navigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = true,
    )

    fun navigateBack()

    fun popTo(
        route: Any,
        inclusive: Boolean = false,
    )
}
