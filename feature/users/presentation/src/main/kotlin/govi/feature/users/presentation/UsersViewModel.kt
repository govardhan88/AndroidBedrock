package govi.feature.users.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.base.viewmodel.BaseViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.users.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val appNavigator: AppNavigator,
) : BaseViewModel<UsersUiState, UsersIntent, UsersEffect>(UsersUiState()) {
    init {
        loadUsers()
    }

    override fun processIntent(intent: UsersIntent) {
        when (intent) {
            is UsersIntent.LoadUsers -> loadUsers()
            is UsersIntent.UserClicked -> onUserClicked(intent.userId)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            updateState { copy(usersState = AsyncState.Loading) }
            getUsersUseCase().fold(
                onSuccess = { users ->
                    updateState { copy(usersState = AsyncState.Success(users)) }
                },
                onFailure = { error ->
                    updateState {
                        copy(
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
