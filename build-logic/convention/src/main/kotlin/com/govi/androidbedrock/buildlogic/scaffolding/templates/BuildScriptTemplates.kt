package com.govi.androidbedrock.buildlogic.scaffolding.templates

object BuildScriptTemplates {
    fun compactBuildGradle(featureName: String): String =
        """
        plugins {
            id("bedrock.android.feature")
        }

        android {
            namespace = "com.govi.androidbedrock.feature.$featureName"
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
            id("bedrock.kotlin.jvm")
        }

        dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.javax.inject)
        }
        """.trimIndent() + "\n"

    fun dataBuildGradle(featureName: String): String =
        """
        plugins {
            id("bedrock.android.library")
            id("bedrock.android.hilt")
        }

        android {
            namespace = "com.govi.androidbedrock.feature.$featureName.data"
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
            id("bedrock.android.feature")
        }

        android {
            namespace = "com.govi.androidbedrock.feature.$featureName.presentation"
            resourcePrefix = "${featureName}_"
        }

        dependencies {
            implementation(project(":feature:$featureName:domain"))
            implementation(project(":core:navigation"))
        }
        """.trimIndent() + "\n"
}
