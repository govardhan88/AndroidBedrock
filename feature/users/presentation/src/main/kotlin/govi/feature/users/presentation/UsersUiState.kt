package govi.feature.users.presentation

import govi.feature.users.domain.model.User

sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<User>) : UsersUiState
    data class Error(val message: String) : UsersUiState
}
