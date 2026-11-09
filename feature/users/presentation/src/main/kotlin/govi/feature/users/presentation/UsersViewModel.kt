package govi.feature.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.users.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val appNavigator: AppNavigator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(usersState = AsyncState.Loading) }
            getUsersUseCase().fold(
                onSuccess = { users ->
                    _uiState.update { it.copy(usersState = AsyncState.Success(users)) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            usersState =
                                AsyncState.Error(
                                    error.message ?: "An unexpected error occurred",
                                ),
                        )
                    }
                },
            )
        }
    }

    fun onUserClicked(userId: String) {
        // appNavigator.navigateTo("user_details/$userId")
    }
}
