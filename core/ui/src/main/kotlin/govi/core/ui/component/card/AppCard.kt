package govi.core.ui.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CardVariant {
    Elevated,
    Outlined,
    Filled
}

/**
 * Standard card container enforcing consistent padding, elevation, and corners.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.large,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    when (variant) {
        CardVariant.Elevated -> {
            Card(
                modifier = clickModifier.fillMaxWidth(),
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
        }
        CardVariant.Outlined -> {
            OutlinedCard(
                modifier = clickModifier.fillMaxWidth(),
                shape = shape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
        }
        CardVariant.Filled -> {
            Card(
                modifier = clickModifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppCard(variant = CardVariant.Elevated) {
                Text("Elevated Card Content", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
