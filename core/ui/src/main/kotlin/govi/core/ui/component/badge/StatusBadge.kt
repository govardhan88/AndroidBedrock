package govi.core.ui.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class StatusVariant {
    Success,
    Warning,
    Error,
    Info,
    Neutral
}

/**
 * Visual status badge / chip for entity states (e.g. Paid, Pending, Active, Failed).
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: StatusVariant = StatusVariant.Neutral,
    showDot: Boolean = true
) {
    val (backgroundColor, contentColor) = when (variant) {
        StatusVariant.Success -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        StatusVariant.Warning -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        StatusVariant.Error -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        StatusVariant.Info -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        StatusVariant.Neutral -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(contentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            StatusBadge(text = "Active", variant = StatusVariant.Success)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(text = "Pending", variant = StatusVariant.Warning)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(text = "Failed", variant = StatusVariant.Error)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(text = "Archived", variant = StatusVariant.Neutral)
        }
    }
}
