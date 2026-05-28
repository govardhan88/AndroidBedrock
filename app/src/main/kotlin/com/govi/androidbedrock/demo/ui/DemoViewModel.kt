package com.govi.androidbedrock.demo.ui

import androidx.lifecycle.viewModelScope
import com.govi.androidbedrock.core.base.BaseViewModel
import com.govi.androidbedrock.core.auth.AuthTokenManager
import com.govi.androidbedrock.core.network.BaseUrlProvider
import com.govi.androidbedrock.core.network.DefaultBaseUrlProvider
import com.govi.androidbedrock.core.utils.DispatchersProvider
import com.govi.androidbedrock.core.utils.provider.DeferredString
import com.govi.androidbedrock.core.utils.provider.deferred
import com.govi.androidbedrock.demo.data.DemoItem
import com.govi.androidbedrock.demo.data.DemoRepository
import com.govi.androidbedrock.demo.ui.navigation.DemoNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val repository: DemoRepository,
    private val authTokenManager: AuthTokenManager,
    private val baseUrlProvider: BaseUrlProvider,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    private val _items = MutableStateFlow<List<DemoItem>>(emptyList())
    val items: StateFlow<List<DemoItem>> = _items.asStateFlow()

    private val _screenTitle = MutableStateFlow<DeferredString>(DemoNavigation.Main.title.deferred())
    val screenTitle: StateFlow<DeferredString> = _screenTitle.asStateFlow()

    private val _navigationStack = MutableStateFlow<List<DemoNavigation>>(listOf(DemoNavigation.Main))
    val navigationStack: StateFlow<List<DemoNavigation>> = _navigationStack.asStateFlow()

    private val _demoOutput = MutableStateFlow<String>("")
    val demoOutput: StateFlow<String> = _demoOutput.asStateFlow()

    fun navigateTo(nav: DemoNavigation) {
        _navigationStack.value = _navigationStack.value + nav
        _screenTitle.value = nav.title.deferred()
        _demoOutput.value = "" // Clear output when navigating
    }

    fun navigateBack() {
        if (_navigationStack.value.size > 1) {
            _navigationStack.value = _navigationStack.value.dropLast(1)
            _screenTitle.value = _navigationStack.value.last().title.deferred()
            _demoOutput.value = ""
        }
    }

    fun fetchDemoItems() {
        launchSafeApi(
            onSuccess = { resultItems ->
                _items.value = resultItems
            },
            block = {
                repository.getDemoItems()
            }
        )
    }

    // --- Networking Demos ---
    fun testNetworkSuccess() {
        launchSafeApi(
            onStart = { _demoOutput.value = "Starting success call..." },
            onSuccess = { result: List<DemoItem> -> 
                _demoOutput.value = "Success: Received ${result.size} items" 
            },
            block = { repository.getDemoItems() }
        )
    }

    fun testNetworkError() {
        launchSafeApi<List<DemoItem>>(
            onStart = { _demoOutput.value = "Starting error call..." },
            onError = { _demoOutput.value = "Caught Error: $it" },
            block = { repository.getMockError() }
        )
    }

    fun testDynamicUrl(newUrl: String) {
        (baseUrlProvider as? DefaultBaseUrlProvider)?.setBaseUrl(newUrl)
        _demoOutput.value = "Base URL updated to: ${baseUrlProvider.getBaseUrl()}"
    }

    // --- Storage Demos ---
    fun saveToken(token: String) {
        authTokenManager.setAccessToken(token)
        _demoOutput.value = "Token saved securely: $token"
    }

    fun readToken() {
        val token = authTokenManager.getAccessToken() ?: "No token found"
        _demoOutput.value = "Read Token: $token"
    }

    // --- Dispatcher Demos ---
    fun testDispatcherSwitch() {
        viewModelScope.launch(dispatchers.main()) {
            _demoOutput.value = "Started on Main: ${Thread.currentThread().name}\n"
            withContext(dispatchers.io()) {
                val ioThread = Thread.currentThread().name
                withContext(dispatchers.main()) {
                    _demoOutput.value += "Switched to IO: $ioThread\n"
                    _demoOutput.value += "Back on Main: ${Thread.currentThread().name}"
                }
            }
        }
    }
}
