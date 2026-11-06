plugins {
    id("govi.android.library")
    id("govi.android.hilt")
}

android {
    namespace = "govi.feature.products.data"
}

dependencies {
    implementation(project(":feature:products:domain"))
    implementation(project(":core:network"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.core)
}
