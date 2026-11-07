package govi.buildlogic.scaffolding.templates

object BuildScriptTemplates {
    fun compactBuildGradle(featureName: String): String =
        """
        plugins {
            id("govi.android.feature")
        }

        android {
            namespace = "govi.feature.$featureName"
            resourcePrefix = "${featureName}_"
        }

        dependencies {
            implementation(project(":core:network"))

            implementation(libs.retrofit.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.okhttp.logging)
            implementation(libs.kotlinx.coroutines.core)
        }
        """.trimIndent() + "\n"

    fun domainBuildGradle(): String =
        """
        plugins {
            id("govi.kotlin.jvm")
        }

        dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.javax.inject)
        }
        """.trimIndent() + "\n"

    fun dataBuildGradle(featureName: String): String =
        """
        plugins {
            id("govi.android.library")
            id("govi.android.hilt")
        }

        android {
            namespace = "govi.feature.$featureName.data"
            resourcePrefix = "${featureName}_"
        }

        dependencies {
            implementation(project(":feature:$featureName:domain"))
            implementation(project(":core:network"))

            implementation(libs.retrofit.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.okhttp.logging)
            implementation(libs.kotlinx.coroutines.core)
        }
        """.trimIndent() + "\n"

    fun presentationBuildGradle(featureName: String): String =
        """
        plugins {
            id("govi.android.feature")
        }

        android {
            namespace = "govi.feature.$featureName.presentation"
            resourcePrefix = "${featureName}_"
        }

        dependencies {
            implementation(project(":feature:$featureName:domain"))
            implementation(project(":core:navigation"))
        }
        """.trimIndent() + "\n"
}
