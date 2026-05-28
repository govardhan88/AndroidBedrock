package com.govi.androidbedrock.demo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.govi.androidbedrock.core.utils.compose.provider.resolve
import com.govi.androidbedrock.demo.ui.dispatchers.DispatchersScreen
import com.govi.androidbedrock.demo.ui.main.MainMenuScreen
import com.govi.androidbedrock.demo.ui.navigation.DemoNavigation
import com.govi.androidbedrock.demo.ui.networking.NetworkingScreen
import com.govi.androidbedrock.demo.ui.resource.ResourceProviderScreen
import com.govi.androidbedrock.demo.ui.storage.StorageScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen(
    viewModel: DemoViewModel = hiltViewModel()
) {
    val navStack by viewModel.navigationStack.collectAsState()
    val screenTitleDeferred by viewModel.screenTitle.collectAsState()
    val currentNav = navStack.last()

    BackHandler(enabled = navStack.size > 1) {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitleDeferred.resolve()) },
                navigationIcon = {
                    if (navStack.size > 1) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentNav) {
                DemoNavigation.Main -> MainMenuScreen(viewModel)
                DemoNavigation.ResourceProvider -> ResourceProviderScreen()
                DemoNavigation.Networking -> NetworkingScreen(viewModel)
                DemoNavigation.Storage -> StorageScreen(viewModel)
                DemoNavigation.Dispatchers -> DispatchersScreen(viewModel)
            }
        }
    }
}
