package com.govi.androidbedrock.demo.ui.networking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.demo.ui.DemoViewModel

@Composable
fun NetworkingScreen(viewModel: DemoViewModel) {
    val output by viewModel.demoOutput.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Networking Demo", style = MaterialTheme.typography.headlineSmall)
            Text("Testing BaseRepository and API Executor patterns.", style = MaterialTheme.typography.bodyMedium)
        }

        item { NetworkSectionHeader("Standard API Calls") }
        item {
            NetworkActionCard("Success Case", "Triggers a safe API call that returns mock items.") {
                viewModel.testNetworkSuccess()
            }
        }
        item {
            NetworkActionCard("Error Case (404)", "Demonstrates automatic error catching and mapping.") {
                viewModel.testNetworkError()
            }
        }

        item { NetworkSectionHeader("Runtime Configuration") }
        item {
            NetworkActionCard("Switch Base URL", "Changes the API region dynamically via DynamicBaseUrlInterceptor.") {
                viewModel.testDynamicUrl("https://api.new-region.com/v2")
            }
        }

        item { NetworkSectionHeader("Results Console") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (output.isEmpty()) "No activity logged..." else output,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun NetworkActionCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
