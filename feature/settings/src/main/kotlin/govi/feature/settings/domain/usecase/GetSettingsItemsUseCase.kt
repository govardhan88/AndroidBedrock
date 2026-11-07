package govi.feature.settings.domain.usecase

import govi.feature.settings.domain.model.SettingsItem
import govi.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsItemsUseCase
    @Inject
    constructor(
        private val repository: SettingsRepository,
    ) {
        suspend operator fun invoke(): Result<List<SettingsItem>> = repository.getItems()
    }
