package govi.feature.settings.domain.model

data class SettingsItem(
    val id: String,
    val title: String,
    val description: String = "",
)
