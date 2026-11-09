plugins {
    id("bedrock.android.library")
    id("bedrock.android.hilt")
}

android {
    namespace = "com.govi.androidbedrock.core.firebase"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
