package com.govi.androidbedrock.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigatorImpl @Inject constructor() : AppNavigator {
    private val channel = Channel<NavigationIntent>(Channel.BUFFERED)

    override val navigationChannel: Flow<NavigationIntent> = channel.receiveAsFlow()

    override fun navigateTo(
        route: Any,
        popUpToRoute: Any?,
        inclusive: Boolean,
        isSingleTop: Boolean,
    ) {
        channel.trySend(NavigationIntent.NavigateTo(route, popUpToRoute, inclusive, isSingleTop))
    }

    override fun navigateBack() {
        channel.trySend(NavigationIntent.NavigateBack)
    }

    override fun popTo(
        route: Any,
        inclusive: Boolean,
    ) {
        channel.trySend(NavigationIntent.PopTo(route, inclusive))
    }
}
