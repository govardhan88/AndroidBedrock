plugins {
    id("bedrock.android.library")
    id("bedrock.android.hilt")
}

android {
    namespace = "com.govi.androidbedrock.core.datastore"
}

dependencies {
    implementation(project(":core:network"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
