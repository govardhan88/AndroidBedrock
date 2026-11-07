package govi.feature.fixture.domain.repository

import govi.feature.fixture.domain.model.FixtureItem

interface FixtureRepository {
    suspend fun getItems(): Result<List<FixtureItem>>

    suspend fun getItemById(id: String): Result<FixtureItem>
}
