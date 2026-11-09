plugins {
    id("bedrock.android.library")
    id("bedrock.android.compose")
    id("bedrock.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.govi.androidbedrock.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
