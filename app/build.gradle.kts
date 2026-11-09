import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("govi.android.compose")
    id("govi.android.hilt")
}

// Dynamically resolves configuration properties from:
// 1. Gradle Project Properties (-P flag, gradle.properties, or ORG_GRADLE_PROJECT_* in CI)
// 2. System Environment Variables (e.g. from GitHub Actions / Docker)
// 3. local.properties (Local developer machine overrides)
// 4. Default fallback value
fun resolveConfigProperty(
    key: String,
    default: String,
): String {
    val fromGradle = providers.gradleProperty(key).orNull?.trim()
    if (!fromGradle.isNullOrEmpty()) return fromGradle

    val fromEnv = providers.environmentVariable(key).orNull?.trim()
    if (!fromEnv.isNullOrEmpty()) return fromEnv

    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        val props = Properties()
        FileInputStream(localFile).use { props.load(it) }
        val fromLocal = props.getProperty(key)?.trim()
        if (!fromLocal.isNullOrEmpty()) return fromLocal
    }

    return default
}

android {
    namespace = "govi.app"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "govi.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val defaultBaseUrl = resolveConfigProperty("BASE_URL", "https://api.example.com/")
        buildConfigField("String", "BASE_URL", "\"$defaultBaseUrl\"")
    }

    buildTypes {
        debug {
            val devUrl = resolveConfigProperty("DEV_BASE_URL", "https://api.dev.example.com/")
            buildConfigField("String", "BASE_URL", "\"$devUrl\"")
        }
        release {
            val prodUrl =
                resolveConfigProperty(
                    "PROD_BASE_URL",
                    resolveConfigProperty("BASE_URL", "https://api.example.com/"),
                )
            buildConfigField("String", "BASE_URL", "\"$prodUrl\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
        abortOnError = true
        warningsAsErrors = true
        checkDependencies = true
    }
}

dependencies {
    // Feature settings (compact)
    implementation(project(":feature:settings"))
    // Feature fixture
    implementation(project(":feature:fixture:presentation"))
    implementation(project(":feature:fixture:data"))
    // Feature products
    implementation(project(":feature:products:presentation"))
    implementation(project(":feature:products:data"))
    implementation(project(":core:base"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":feature:users:presentation"))
    implementation(project(":feature:users:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
