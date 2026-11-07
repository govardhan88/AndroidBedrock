package govi.feature.settings.domain.repository

import govi.feature.settings.domain.model.SettingsItem

interface SettingsRepository {
    suspend fun getItems(): Result<List<SettingsItem>>

    suspend fun getItemById(id: String): Result<SettingsItem>
}
