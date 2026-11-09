plugins {
    id("bedrock.android.library")
    id("bedrock.android.compose")
}

android {
    namespace = "com.govi.androidbedrock.core.ui"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    api(libs.androidx.compose.material.icons.extended)
}
