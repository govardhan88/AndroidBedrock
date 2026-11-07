package govi.core.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import govi.core.ui.component.badge.StatusBadge
import govi.core.ui.component.badge.StatusVariant
import govi.core.ui.component.bar.AppTopBar
import govi.core.ui.component.button.AppOutlinedButton
import govi.core.ui.component.button.AppTextButton
import govi.core.ui.component.button.PrimaryButton
import govi.core.ui.component.button.SecondaryButton
import govi.core.ui.component.card.AppCard
import govi.core.ui.component.card.AppDivider
import govi.core.ui.component.card.AppListItem
import govi.core.ui.component.card.CardVariant
import govi.core.ui.component.dialog.AppConfirmationDialog
import govi.core.ui.component.input.AppPasswordTextField
import govi.core.ui.component.input.AppSearchField
import govi.core.ui.component.input.AppTextField
import govi.core.ui.component.nointernet.NoInternetBanner
import govi.core.ui.component.nointernet.NoInternetBottomSheet
import govi.core.ui.component.nointernet.NoInternetDialog
import govi.core.ui.component.shimmer.ShimmerListItem
import govi.core.ui.theme.AppTheme

/**
 * Design System catalog showcase for inspecting and testing all UI components.
 */
@Composable
fun DesignSystemShowcase() {
    var textValue by remember { mutableStateOf("John Doe") }
    var passwordValue by remember { mutableStateOf("secret") }
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showNoInternetSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(title = "Design System Showcase")
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 1. Buttons
            ShowcaseSection(title = "Buttons") {
                PrimaryButton(text = "Primary Button", onClick = {}, modifier = Modifier.fillMaxWidth())
                PrimaryButton(text = "Loading Button", onClick = {}, isLoading = true, modifier = Modifier.fillMaxWidth())
                SecondaryButton(text = "Secondary Tonal Button", onClick = {}, modifier = Modifier.fillMaxWidth())
                AppOutlinedButton(text = "Outlined Button", onClick = {}, modifier = Modifier.fillMaxWidth())
                AppTextButton(text = "Text Button", onClick = {})
            }

            AppDivider()

            // 2. Input Fields
            ShowcaseSection(title = "Input Fields") {
                AppSearchField(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth())
                AppTextField(value = textValue, onValueChange = { textValue = it }, label = "Full Name", modifier = Modifier.fillMaxWidth())
                AppTextField(value = "bad@", onValueChange = {
                }, label = "Email", isError = true, errorMessage = "Invalid email address format", modifier = Modifier.fillMaxWidth())
                AppPasswordTextField(value = passwordValue, onValueChange = { passwordValue = it }, modifier = Modifier.fillMaxWidth())
            }

            AppDivider()

            // 3. Status Badges
            ShowcaseSection(title = "Status Badges") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(text = "Active", variant = StatusVariant.Success)
                    StatusBadge(text = "Pending", variant = StatusVariant.Warning)
                    StatusBadge(text = "Failed", variant = StatusVariant.Error)
                    StatusBadge(text = "Info", variant = StatusVariant.Info)
                }
            }

            AppDivider()

            // 4. Cards & List Items
            ShowcaseSection(title = "Cards & List Items") {
                AppCard(variant = CardVariant.Elevated) {
                    Text("Elevated AppCard", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Enforces standard padding and elevation.", style = MaterialTheme.typography.bodyMedium)
                }

                AppCard(variant = CardVariant.Outlined) {
                    AppListItem(
                        headline = "Alice Johnson",
                        supportingText = "Lead Android Architect",
                        leadingContent = { androidx.compose.material3.Icon(Icons.Default.Person, contentDescription = null) },
                        onClick = {},
                    )
                }
            }

            AppDivider()

            // 5. Skeleton Shimmer
            ShowcaseSection(title = "Shimmer Placeholders") {
                ShimmerListItem()
            }

            // 6. Dialog trigger
            ShowcaseSection(title = "Dialogs") {
                PrimaryButton(text = "Open Confirmation Dialog", onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth())
            }

            AppDivider()

            // 7. No Internet Formats
            ShowcaseSection(title = "No Internet State Formats (Default: Dialog)") {
                Text(
                    text = "Sticky Banner Format:",
                    style = MaterialTheme.typography.labelLarge,
                )
                NoInternetBanner(
                    isVisible = true,
                    onRetry = {},
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Launch No Internet Dialog (Default)",
                    onClick = { showNoInternetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                SecondaryButton(
                    text = "Launch No Internet BottomSheet",
                    onClick = { showNoInternetSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (showDialog) {
                AppConfirmationDialog(
                    title = "Confirm Action",
                    message = "Are you sure you want to proceed with this operation?",
                    confirmLabel = "Confirm",
                    onConfirm = { showDialog = false },
                    onDismiss = { showDialog = false },
                )
            }

            if (showNoInternetDialog) {
                NoInternetDialog(
                    onRetry = { showNoInternetDialog = false },
                    onDismiss = { showNoInternetDialog = false },
                )
            }

            if (showNoInternetSheet) {
                NoInternetBottomSheet(
                    onRetry = { showNoInternetSheet = false },
                    onDismissRequest = { showNoInternetSheet = false },
                )
            }
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun DesignSystemShowcasePreview() {
    AppTheme {
        DesignSystemShowcase()
    }
}
