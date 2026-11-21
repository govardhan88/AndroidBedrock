plugins {
    id("bedrock.android.feature")
}

android {
    namespace = "com.govi.androidbedrock.feature.demo"
    resourcePrefix = "demo_"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
