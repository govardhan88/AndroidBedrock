plugins {
    id("govi.android.library")
    id("govi.android.compose")
    id("govi.android.hilt")
}

android {
    namespace = "govi.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
