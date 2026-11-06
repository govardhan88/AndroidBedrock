plugins {
    id("govi.android.library")
    id("govi.android.hilt")
}

android {
    namespace = "govi.feature.users.data"
}

dependencies {
    implementation(project(":feature:users:domain"))
    implementation(project(":core:network"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.core)
}
