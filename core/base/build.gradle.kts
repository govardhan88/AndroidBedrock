plugins {
    id("bedrock.android.library")
    id("bedrock.android.hilt")
}

android {
    namespace = "com.govi.androidbedrock.core.base"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
