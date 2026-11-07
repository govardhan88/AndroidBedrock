plugins {
    id("govi.android.feature")
}

android {
    namespace = "govi.feature.settings"
    resourcePrefix = "settings_"
}

dependencies {
    implementation(project(":core:network"))

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.core)
}
