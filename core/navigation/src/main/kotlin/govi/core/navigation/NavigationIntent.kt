package govi.core.navigation

sealed interface NavigationIntent {
    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = true
    ) : NavigationIntent

    data object NavigateBack : NavigationIntent

    data class PopTo(
        val route: String,
        val inclusive: Boolean = false
    ) : NavigationIntent
}
