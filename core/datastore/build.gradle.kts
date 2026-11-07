plugins {
    id("govi.android.library")
    id("govi.android.hilt")
}

android {
    namespace = "govi.core.datastore"
}

dependencies {
    implementation(project(":core:network"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
