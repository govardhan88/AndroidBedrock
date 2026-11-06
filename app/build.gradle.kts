plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("govi.android.compose")
    id("govi.android.hilt")
}

android {
    namespace = "govi.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "govi.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    // Feature products
    implementation(project(":feature:products:presentation"))
    implementation(project(":feature:products:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":feature:users:presentation"))
    implementation(project(":feature:users:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
