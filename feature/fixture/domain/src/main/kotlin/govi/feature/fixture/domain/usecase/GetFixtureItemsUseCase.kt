package govi.feature.fixture.domain.usecase

import govi.feature.fixture.domain.model.FixtureItem
import govi.feature.fixture.domain.repository.FixtureRepository
import javax.inject.Inject

class GetFixtureItemsUseCase @Inject constructor(
    private val repository: FixtureRepository,
) {
    suspend operator fun invoke(): Result<List<FixtureItem>> = repository.getItems()
}
