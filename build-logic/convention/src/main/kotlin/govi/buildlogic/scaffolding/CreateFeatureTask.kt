package govi.buildlogic.scaffolding

import govi.buildlogic.scaffolding.templates.BuildScriptTemplates
import govi.buildlogic.scaffolding.templates.DataTemplates
import govi.buildlogic.scaffolding.templates.DomainTemplates
import govi.buildlogic.scaffolding.templates.PresentationTemplates
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Locale

abstract class CreateFeatureTask : DefaultTask() {
    init {
        group = "scaffolding"
        description = "Scaffolds a new Clean Architecture feature in either divided (3 submodules) or compact (single module) mode"
    }

    @TaskAction
    fun execute() {
        val rawName =
            project.gradle.startParameter.projectProperties["name"]
                ?: project.providers
                    .gradleProperty("name")
                    .orNull
                    ?.takeIf { it != project.name }

        if (rawName.isNullOrBlank()) {
            throw GradleException(
                """
                Missing required parameter 'name'.
                Usage:
                  Divided (3 submodules - default):
                    ./gradlew createFeature -Pname=<feature_name>
                    Example: ./gradlew createFeature -Pname=orders

                  Compact (single module):
                    ./gradlew createFeature -Pname=<feature_name> -Ptype=compact
                    Example: ./gradlew createFeature -Pname=settings -Ptype=compact
                """.trimIndent(),
            )
        }

        val rawType =
            project.gradle.startParameter.projectProperties["type"]
                ?: project.providers.gradleProperty("type").orNull
                ?: if (project.gradle.startParameter.projectProperties["compact"] == "true" ||
                    project.providers.gradleProperty("compact").orNull == "true"
                ) {
                    "compact"
                } else {
                    "divided"
                }

        val isCompact = rawType.equals("compact", ignoreCase = true) || rawType.equals("simple", ignoreCase = true)

        val featureName = rawName.lowercase(Locale.ROOT)
        val className =
            featureName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }

        val rootDir = project.rootDir
        val featureRoot = File(rootDir, "feature/$featureName")
        if (featureRoot.exists()) {
            throw GradleException("Feature directory already exists at: ${featureRoot.path}")
        }

        val domainPackagePath = "govi/feature/$featureName/domain"
        val dataPackagePath = "govi/feature/$featureName/data"
        val presentationPackagePath = "govi/feature/$featureName/presentation"

        val domainSrcDir: File
        val dataSrcDir: File
        val presentationSrcDir: File

        if (isCompact) {
            domainSrcDir = File(rootDir, "feature/$featureName/src/main/kotlin/$domainPackagePath")
            dataSrcDir = File(rootDir, "feature/$featureName/src/main/kotlin/$dataPackagePath")
            presentationSrcDir = File(rootDir, "feature/$featureName/src/main/kotlin/$presentationPackagePath")

            domainSrcDir.mkdirs()
            dataSrcDir.mkdirs()
            presentationSrcDir.mkdirs()

            File(rootDir, "feature/$featureName/build.gradle.kts").writeText(
                BuildScriptTemplates.compactBuildGradle(featureName),
            )
        } else {
            val domainDir = File(rootDir, "feature/$featureName/domain")
            val dataDir = File(rootDir, "feature/$featureName/data")
            val presentationDir = File(rootDir, "feature/$featureName/presentation")

            domainSrcDir = File(rootDir, "feature/$featureName/domain/src/main/kotlin/$domainPackagePath")
            dataSrcDir = File(rootDir, "feature/$featureName/data/src/main/kotlin/$dataPackagePath")
            presentationSrcDir = File(rootDir, "feature/$featureName/presentation/src/main/kotlin/$presentationPackagePath")

            domainSrcDir.mkdirs()
            dataSrcDir.mkdirs()
            presentationSrcDir.mkdirs()

            File(domainDir, "build.gradle.kts").writeText(BuildScriptTemplates.domainBuildGradle())
            File(dataDir, "build.gradle.kts").writeText(BuildScriptTemplates.dataBuildGradle(featureName))
            File(presentationDir, "build.gradle.kts").writeText(BuildScriptTemplates.presentationBuildGradle(featureName))
        }

        // Domain layer files
        File(domainSrcDir, "model/${className}Item.kt").apply {
            parentFile.mkdirs()
            writeText(DomainTemplates.model(featureName, className))
        }

        File(domainSrcDir, "repository/${className}Repository.kt").apply {
            parentFile.mkdirs()
            writeText(DomainTemplates.repository(featureName, className))
        }

        File(domainSrcDir, "usecase/Get${className}ItemsUseCase.kt").apply {
            parentFile.mkdirs()
            writeText(DomainTemplates.useCase(featureName, className))
        }

        // Data layer files
        File(dataSrcDir, "remote/${className}Dto.kt").apply {
            parentFile.mkdirs()
            writeText(DataTemplates.dto(featureName, className))
        }

        File(dataSrcDir, "remote/${className}ApiService.kt").apply {
            parentFile.mkdirs()
            writeText(DataTemplates.apiService(featureName, className))
        }

        File(dataSrcDir, "repository/${className}RepositoryImpl.kt").apply {
            parentFile.mkdirs()
            writeText(DataTemplates.repositoryImpl(featureName, className))
        }

        File(dataSrcDir, "di/${className}DataModule.kt").apply {
            parentFile.mkdirs()
            writeText(DataTemplates.dataModule(featureName, className))
        }

        // Presentation layer files
        File(presentationSrcDir, "${className}UiState.kt").apply {
            parentFile.mkdirs()
            writeText(PresentationTemplates.uiState(featureName, className))
        }

        File(presentationSrcDir, "${className}ViewModel.kt").apply {
            parentFile.mkdirs()
            writeText(PresentationTemplates.viewModel(featureName, className))
        }

        File(presentationSrcDir, "${className}Screen.kt").apply {
            parentFile.mkdirs()
            writeText(PresentationTemplates.screen(featureName, className))
        }

        File(presentationSrcDir, "navigation/${className}FeatureEntryImpl.kt").apply {
            parentFile.mkdirs()
            writeText(PresentationTemplates.featureEntry(featureName, className))
        }

        File(presentationSrcDir, "di/${className}NavigationModule.kt").apply {
            parentFile.mkdirs()
            writeText(PresentationTemplates.navigationModule(featureName, className))
        }

        // Register route in core:navigation AppRoute.kt
        val appRouteFile = File(rootDir, "core/navigation/src/main/kotlin/govi/core/navigation/route/AppRoute.kt")
        if (appRouteFile.exists()) {
            var appRouteContent = appRouteFile.readText()
            val routeDeclaration = "data object $className : AppRoute"
            if (!appRouteContent.contains(routeDeclaration)) {
                val lastBraceIndex = appRouteContent.lastIndexOf('}')
                if (lastBraceIndex != -1) {
                    val codeToInsert = "    @Serializable\n    data object $className : AppRoute\n"
                    appRouteContent =
                        appRouteContent.substring(0, lastBraceIndex) +
                        codeToInsert +
                        appRouteContent.substring(lastBraceIndex)
                    appRouteFile.writeText(appRouteContent)
                    println("✓ Registered type-safe destination AppRoute.$className in AppRoute.kt")
                }
            }
        }

        // Update settings.gradle.kts
        val settingsFile = File(rootDir, "settings.gradle.kts")
        val settingsContent = settingsFile.readText()
        val modulesToAdd =
            if (isCompact) {
                listOf(":feature:$featureName")
            } else {
                listOf(
                    ":feature:$featureName:domain",
                    ":feature:$featureName:data",
                    ":feature:$featureName:presentation",
                )
            }.filterNot { settingsContent.contains("\"$it\"") }

        if (modulesToAdd.isNotEmpty()) {
            val appendIncludes = modulesToAdd.joinToString("\n") { "include(\"$it\")" }
            val typeLabel = if (isCompact) "compact" else "divided"
            settingsFile.appendText("\n// Feature: $featureName ($typeLabel)\n$appendIncludes\n")
            println("✓ Added modules to settings.gradle.kts: $modulesToAdd")
        }

        // Update app/build.gradle.kts
        val appBuildFile = File(rootDir, "app/build.gradle.kts")
        if (appBuildFile.exists()) {
            var appContent = appBuildFile.readText()
            val depsToInsertList =
                if (isCompact) {
                    listOf("implementation(project(\":feature:$featureName\"))")
                } else {
                    listOf(
                        "implementation(project(\":feature:$featureName:presentation\"))",
                        "implementation(project(\":feature:$featureName:data\"))",
                    )
                }.filterNot { appContent.contains(it) }

            if (depsToInsertList.isNotEmpty()) {
                val dependenciesIndex = appContent.indexOf("dependencies {")
                if (dependenciesIndex != -1) {
                    val insertIndex = dependenciesIndex + "dependencies {".length
                    val typeLabel = if (isCompact) "compact" else "divided"
                    val depsToInsert =
                        buildString {
                            append("\n    // Feature $featureName ($typeLabel)")
                            depsToInsertList.forEach { append("\n    $it") }
                        }
                    appContent = appContent.substring(0, insertIndex) + depsToInsert + appContent.substring(insertIndex)
                    appBuildFile.writeText(appContent)
                    println("✓ Injected dependencies into app/build.gradle.kts: $depsToInsertList")
                }
            }
        }

        if (isCompact) {
            println(
                "✓ Successfully created compact feature '$featureName' (:feature:$featureName) with internal domain, data, and presentation layers!",
            )
        } else {
            println(
                "✓ Successfully created divided feature '$featureName' with Clean Architecture (:domain, :data, :presentation) submodules!",
            )
        }
    }
}
