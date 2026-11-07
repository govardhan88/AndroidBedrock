plugins {
    `kotlin-dsl`
}

group = "govi.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "govi.android.library"
            implementationClass = "govi.buildlogic.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "govi.android.compose"
            implementationClass = "govi.buildlogic.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "govi.android.hilt"
            implementationClass = "govi.buildlogic.convention.AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "govi.android.feature"
            implementationClass = "govi.buildlogic.convention.AndroidFeatureConventionPlugin"
        }
        register("kotlinJvm") {
            id = "govi.kotlin.jvm"
            implementationClass = "govi.buildlogic.convention.KotlinJvmConventionPlugin"
        }
        register("scaffolding") {
            id = "govi.scaffolding"
            implementationClass = "govi.buildlogic.scaffolding.ScaffoldingPlugin"
        }
    }
}
