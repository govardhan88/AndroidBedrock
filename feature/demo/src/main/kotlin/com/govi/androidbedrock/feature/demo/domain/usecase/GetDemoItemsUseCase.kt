package com.govi.androidbedrock.feature.demo.domain.usecase

import com.govi.androidbedrock.feature.demo.domain.model.DemoItem
import com.govi.androidbedrock.feature.demo.domain.repository.DemoRepository
import javax.inject.Inject

class GetDemoItemsUseCase @Inject constructor(
    private val repository: DemoRepository,
) {
    suspend operator fun invoke(): Result<List<DemoItem>> = repository.getItems()
}
