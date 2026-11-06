plugins {
    id("govi.android.library")
    id("govi.android.compose")
}

android {
    namespace = "govi.core.ui"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
