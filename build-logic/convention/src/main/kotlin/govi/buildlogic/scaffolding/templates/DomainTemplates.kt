package govi.buildlogic.scaffolding.templates

object DomainTemplates {
    fun model(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.domain.model

        data class ${className}Item(
            val id: String,
            val title: String,
            val description: String = "",
        )
        """.trimIndent() + "\n"

    fun repository(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.domain.repository

        import govi.feature.$featureName.domain.model.${className}Item

        interface ${className}Repository {
            suspend fun getItems(): Result<List<${className}Item>>

            suspend fun getItemById(id: String): Result<${className}Item>
        }
        """.trimIndent() + "\n"

    fun useCase(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.domain.usecase

        import govi.feature.$featureName.domain.model.${className}Item
        import govi.feature.$featureName.domain.repository.${className}Repository
        import javax.inject.Inject

        class Get${className}ItemsUseCase
            @Inject
            constructor(
                private val repository: ${className}Repository,
            ) {
                suspend operator fun invoke(): Result<List<${className}Item>> = repository.getItems()
            }
        """.trimIndent() + "\n"
}
