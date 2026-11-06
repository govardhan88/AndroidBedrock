plugins {
    id("govi.android.library")
    id("govi.android.hilt")
}

android {
    namespace = "govi.feature.fixture.data"
}

dependencies {
    implementation(project(":feature:fixture:domain"))
    implementation(project(":core:network"))

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.core)
}
