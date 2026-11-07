package govi.core.navigation

sealed interface NavigationIntent {
    data class NavigateTo(
        val route: Any,
        val popUpToRoute: Any? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = true,
    ) : NavigationIntent

    data object NavigateBack : NavigationIntent

    data class PopTo(
        val route: Any,
        val inclusive: Boolean = false,
    ) : NavigationIntent
}
