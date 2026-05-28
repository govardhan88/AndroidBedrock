package com.govi.androidbedrock.demo.ui.dispatchers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.demo.ui.DemoViewModel

@Composable
fun DispatchersScreen(viewModel: DemoViewModel) {
    val output by viewModel.demoOutput.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Dispatchers Provider Demo", style = MaterialTheme.typography.headlineSmall)
            Text("Testing injected Coroutine Dispatchers for cross-platform testability.", style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Execution Sequence:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "1. UI Update (Main)\n2. Heavy Task (IO)\n3. UI Refresh (Main)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.testDispatcherSwitch() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify Thread Switching")
                    }
                }
            }
        }

        item {
            Text("Injected Logs:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(text = output, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        item {
            Text(
                "Why use DispatchersProvider?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Hardcoding Dispatchers.IO makes unit tests flaky and slow. " +
                "By injecting them, we can swap real threads for TestDispatchers in milliseconds.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
