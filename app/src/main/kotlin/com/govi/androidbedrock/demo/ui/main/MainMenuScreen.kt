package com.govi.androidbedrock.demo.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.govi.androidbedrock.demo.ui.DemoViewModel
import com.govi.androidbedrock.demo.ui.navigation.DemoNavigation

@Composable
fun MainMenuScreen(viewModel: DemoViewModel) {
    val options = listOf(
        DemoNavigation.ResourceProvider,
        DemoNavigation.Networking,
        DemoNavigation.Storage,
        DemoNavigation.Dispatchers
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(options) { option ->
            ListItem(
                headlineContent = { Text(option.title) },
                modifier = Modifier.clickable { viewModel.navigateTo(option) },
                trailingContent = { Text(">") }
            )
            HorizontalDivider()
        }
    }
}
