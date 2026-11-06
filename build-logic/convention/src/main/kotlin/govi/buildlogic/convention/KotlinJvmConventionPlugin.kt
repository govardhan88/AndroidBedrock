package govi.buildlogic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinJvmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureKotlinJvm()

            dependencies {
                add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
                add("testImplementation", libs.findLibrary("junit").get())
            }
        }
    }
}
