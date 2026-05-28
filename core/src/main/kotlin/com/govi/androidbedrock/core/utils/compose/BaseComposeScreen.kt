package com.govi.androidbedrock.core.utils.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.govi.androidbedrock.core.base.BaseViewModel

/**
 * A wrapper Composable that automatically handles Loading and Error states from a [BaseViewModel].
 */
@Composable
fun <VM : BaseViewModel> BaseComposeScreen(
    viewModel: VM,
    onRetry: () -> Unit = {},
    content: @Composable (VM) -> Unit
) {
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        } else {
            content(viewModel)
        }
    }
}
