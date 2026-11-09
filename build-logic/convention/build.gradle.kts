plugins {
    `kotlin-dsl`
}

group = "com.govi.androidbedrock.buildlogic"

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
            id = "bedrock.android.library"
            implementationClass = "com.govi.androidbedrock.buildlogic.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "bedrock.android.compose"
            implementationClass = "com.govi.androidbedrock.buildlogic.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "bedrock.android.hilt"
            implementationClass = "com.govi.androidbedrock.buildlogic.convention.AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "bedrock.android.feature"
            implementationClass = "com.govi.androidbedrock.buildlogic.convention.AndroidFeatureConventionPlugin"
        }
        register("kotlinJvm") {
            id = "bedrock.kotlin.jvm"
            implementationClass = "com.govi.androidbedrock.buildlogic.convention.KotlinJvmConventionPlugin"
        }
        register("scaffolding") {
            id = "bedrock.scaffolding"
            implementationClass = "com.govi.androidbedrock.buildlogic.scaffolding.ScaffoldingPlugin"
        }
    }
}
