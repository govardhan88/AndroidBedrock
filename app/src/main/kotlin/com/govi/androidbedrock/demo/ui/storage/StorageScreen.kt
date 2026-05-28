package com.govi.androidbedrock.demo.ui.storage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.demo.ui.DemoViewModel

@Composable
fun StorageScreen(viewModel: DemoViewModel) {
    val output by viewModel.demoOutput.collectAsState()
    var inputToken by remember { mutableStateOf("bedrock_secret_999") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Secure Storage Demo", style = MaterialTheme.typography.headlineSmall)
            Text("Powered by EncryptedSharedPreferences for hardware-level security.", style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = inputToken,
                        onValueChange = { inputToken = it },
                        label = { Text("Secret Token") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveToken(inputToken) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                        Button(
                            onClick = { viewModel.readToken() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Read")
                        }
                    }
                }
            }
        }

        item {
            Text("Persistence Logic", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(
                "Data is encrypted at rest using AES-256. Even on rooted devices, " +
                "the keys are stored in the Android Keystore system.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = "Result: $output",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
