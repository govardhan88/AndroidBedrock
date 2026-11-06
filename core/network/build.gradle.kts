plugins {
    id("govi.android.library")
    id("govi.android.hilt")
}

android {
    namespace = "govi.core.network"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.core)
}
