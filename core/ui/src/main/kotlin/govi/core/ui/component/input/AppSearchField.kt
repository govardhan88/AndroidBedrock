package govi.core.ui.component.input

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standard Search input field with search action and clear button.
 */
@Composable
fun AppSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: ((String) -> Unit)? = null,
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    AppTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        singleLine = true,
        enabled = enabled,
        showClearButton = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    onSearch?.invoke(query)
                },
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun AppSearchFieldPreview() {
    MaterialTheme {
        AppSearchField(
            query = "Android Compose",
            onQueryChange = {},
        )
    }
}
