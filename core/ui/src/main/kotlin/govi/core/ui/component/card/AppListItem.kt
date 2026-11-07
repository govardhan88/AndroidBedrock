package govi.core.ui.component.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Standard list item row for menus, directories, settings, and detail screens.
 */
@Composable
fun AppListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    overlineText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    },
    onClick: (() -> Unit)? = null,
) {
    val clickModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    ListItem(
        headlineContent = {
            Text(
                text = headline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        modifier = clickModifier.fillMaxWidth(),
        supportingContent =
            supportingText?.let {
                {
                    Text(
                        text = it,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        overlineContent =
            overlineText?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors =
            ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun AppListItemPreview() {
    MaterialTheme {
        AppListItem(
            headline = "Account Profile",
            supportingText = "Manage personal information and credentials",
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            onClick = {},
        )
    }
}
