package com.govi.androidbedrock.buildlogic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("bedrock.android.library")
                apply("bedrock.android.compose")
                apply("bedrock.android.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", project(":core:base"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:firebase"))
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
