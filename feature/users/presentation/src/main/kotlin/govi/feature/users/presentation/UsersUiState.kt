package govi.feature.users.presentation

import govi.core.base.mvi.UiEffect
import govi.core.base.mvi.UiIntent
import govi.core.base.mvi.UiState
import govi.core.ui.AsyncState
import govi.feature.users.domain.model.User

data class UsersUiState(
    val usersState: AsyncState<List<User>> = AsyncState.Loading,
    val searchQuery: String = "",
) : UiState

sealed interface UsersIntent : UiIntent {
    data object LoadUsers : UsersIntent

    data class UserClicked(
        val userId: String,
    ) : UsersIntent
}

sealed interface UsersEffect : UiEffect {
    data class NavigateToDetails(
        val userId: String,
    ) : UsersEffect

    data class ShowToast(
        val message: String,
    ) : UsersEffect
}
