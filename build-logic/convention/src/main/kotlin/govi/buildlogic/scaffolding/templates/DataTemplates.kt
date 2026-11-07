package govi.buildlogic.scaffolding.templates

object DataTemplates {
    fun dto(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.data.remote

        import govi.feature.$featureName.domain.model.${className}Item
        import kotlinx.serialization.SerialName
        import kotlinx.serialization.Serializable

        @Serializable
        data class ${className}Dto(
            @SerialName("id") val id: String,
            @SerialName("title") val title: String,
            @SerialName("description") val description: String? = null,
        ) {
            fun toDomain(): ${className}Item =
                ${className}Item(
                    id = id,
                    title = title,
                    description = description.orEmpty(),
                )
        }
        """.trimIndent() + "\n"

    fun apiService(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.data.remote

        import retrofit2.Response
        import retrofit2.http.GET
        import retrofit2.http.Path

        interface ${className}ApiService {
            @GET("$featureName")
            suspend fun getItems(): Response<List<${className}Dto>>

            @GET("$featureName/{id}")
            suspend fun getItemById(
                @Path("id") id: String,
            ): Response<${className}Dto>
        }
        """.trimIndent() + "\n"

    fun repositoryImpl(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.data.repository

        import govi.core.network.NetworkResult
        import govi.core.network.safeApiCall
        import govi.feature.$featureName.data.remote.${className}ApiService
        import govi.feature.$featureName.domain.model.${className}Item
        import govi.feature.$featureName.domain.repository.${className}Repository
        import javax.inject.Inject

        class ${className}RepositoryImpl
            @Inject
            constructor(
                private val apiService: ${className}ApiService,
            ) : ${className}Repository {
                override suspend fun getItems(): Result<List<${className}Item>> =
                    when (val result = safeApiCall { apiService.getItems() }) {
                        is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
                        is NetworkResult.Error -> Result.failure(Exception("HTTP ${"$"}{result.code}: ${"$"}{result.message}"))
                        is NetworkResult.Exception -> Result.failure(result.throwable)
                    }

                override suspend fun getItemById(id: String): Result<${className}Item> =
                    when (val result = safeApiCall { apiService.getItemById(id) }) {
                        is NetworkResult.Success -> Result.success(result.data.toDomain())
                        is NetworkResult.Error -> Result.failure(Exception("HTTP ${"$"}{result.code}: ${"$"}{result.message}"))
                        is NetworkResult.Exception -> Result.failure(result.throwable)
                    }
            }
        """.trimIndent() + "\n"

    fun dataModule(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.data.di

        import dagger.Binds
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import govi.feature.$featureName.data.remote.${className}ApiService
        import govi.feature.$featureName.data.repository.${className}RepositoryImpl
        import govi.feature.$featureName.domain.repository.${className}Repository
        import retrofit2.Retrofit
        import javax.inject.Singleton

        @Module
        @InstallIn(SingletonComponent::class)
        abstract class ${className}DataModule {
            @Binds
            @Singleton
            abstract fun bind${className}Repository(impl: ${className}RepositoryImpl): ${className}Repository

            companion object {
                @Provides
                @Singleton
                fun provide${className}ApiService(retrofit: Retrofit): ${className}ApiService = retrofit.create(${className}ApiService::class.java)
            }
        }
        """.trimIndent() + "\n"
}
