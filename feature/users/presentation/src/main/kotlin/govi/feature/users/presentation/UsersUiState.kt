package govi.feature.users.presentation

import govi.core.ui.AsyncState
import govi.feature.users.domain.model.User

data class UsersUiState(
    val usersState: AsyncState<List<User>> = AsyncState.Loading,
    val searchQuery: String = ""
)
