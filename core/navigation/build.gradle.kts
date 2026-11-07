plugins {
    id("govi.android.library")
    id("govi.android.compose")
    id("govi.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "govi.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
