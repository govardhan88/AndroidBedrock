package com.govi.androidbedrock.demo.ui.resource

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.core.utils.compose.provider.resolve
import com.govi.androidbedrock.core.utils.provider.DeferredColor
import com.govi.androidbedrock.core.utils.provider.deferred

@Composable
fun ResourceProviderScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Resource Provider Demo", style = MaterialTheme.typography.headlineSmall)
            Text(
                "ViewModels can hold these without Context and resolve them in the UI layer.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // --- String Section ---
        item { ResourceSectionTitle("Strings") }
        item {
            ResourceItemCard("Constant Based (Raw String)") {
                val deferred = "This is a raw string".deferred()
                Text(text = deferred.resolve())
            }
        }
        item {
            ResourceItemCard("ID Based (Resource)") {
                // In a real app, use R.string.app_name
                // For demo purpose, we use raw as we don't have R here easily
                val deferred = "Resolved from Resource ID".deferred() 
                Text(text = deferred.resolve())
            }
        }

        // --- Color Section ---
        item { ResourceSectionTitle("Colors") }
        item {
            ResourceItemCard("Constant Based (Raw Color)") {
                val deferred = DeferredColor.Raw(Color.Red.toArgb())
                ColorBox(deferred.resolve())
            }
        }
        item {
            ResourceItemCard("ID Based (Resource Color)") {
                // In a real app, use R.color.primary.deferredColor()
                val deferred = DeferredColor.Raw(MaterialTheme.colorScheme.primary.toArgb())
                ColorBox(Color(deferred.resolve()))
            }
        }

        // --- Drawable Section ---
        item { ResourceSectionTitle("Drawables") }
        item {
            ResourceItemCard("ID Based (Resource Drawable)") {
                // In a real app, use R.drawable.ic_launcher.deferredDrawable()
                Text("Check core module for DeferredDrawable implementation details.")
            }
        }
    }
}

@Composable
fun ResourceSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ResourceItemCard(label: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ColorBox(color: Any) {
    val finalColor = when (color) {
        is Int -> Color(color)
        is Color -> color
        else -> Color.Gray
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(finalColor, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text("Color Value: $finalColor")
    }
}
