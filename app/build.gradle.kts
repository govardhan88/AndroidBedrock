import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("bedrock.android.compose")
    id("bedrock.android.hilt")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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

        val altKey =
            if (key.contains('_')) {
                key.lowercase().replace('_', '.')
            } else {
                key.uppercase().replace('.', '_')
            }
        val fromAltLocal = props.getProperty(altKey)?.trim()
        if (!fromAltLocal.isNullOrEmpty()) return fromAltLocal
    }

    return default
}

fun setupGoogleServicesJson() {
    val targetFile = file("google-services.json")

    // 1. Check if a custom file path is configured in local.properties or env
    val customPath = resolveConfigProperty("GOOGLE_SERVICES_JSON_PATH", "")
    if (customPath.isNotEmpty()) {
        val srcFile = rootProject.file(customPath).takeIf { it.exists() } ?: file(customPath)
        if (srcFile.exists()) {
            srcFile.copyTo(targetFile, overwrite = true)
            return
        }
    }

    // 2. Check if raw JSON content is configured in local.properties, gradle properties, or env
    val rawJson = resolveConfigProperty("GOOGLE_SERVICES_JSON", "")
    if (rawJson.isNotEmpty()) {
        targetFile.writeText(rawJson)
        return
    }

    // 3. If target file already exists (e.g. dropped manually by developer or injected by CI), preserve it
    if (targetFile.exists()) {
        return
    }

    // 4. Default fallback: generate starter placeholder so builds never fail when unconfigured
    targetFile.writeText(
        """
        {
          "project_info": {
            "project_number": "000000000000",
            "project_id": "android-bedrock-starter",
            "storage_bucket": "android-bedrock-starter.appspot.com"
          },
          "client": [
            {
              "client_info": {
                "mobilesdk_app_id": "1:000000000000:android:0000000000000000000000",
                "android_client_info": {
                  "package_name": "com.govi.androidbedrock"
                }
              },
              "oauth_client": [],
              "api_key": [
                {
                  "current_key": "AIzaSyStarterMockKeyLocalCIBuildPassing0"
                }
              ],
              "services": {
                "appinvite_service": {
                  "other_platform_oauth_client": []
                }
              }
            }
          ],
          "configuration_version": "1"
        }
        """.trimIndent() + "\n",
    )
}

setupGoogleServicesJson()

android {
    namespace = "com.govi.androidbedrock"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.govi.androidbedrock"
        minSdk = 24
        targetSdk = 35

        val explicitCode = resolveConfigProperty("VERSION_CODE", "").toIntOrNull()
        val baseCode = resolveConfigProperty("BASE_VERSION_CODE", "1000").toIntOrNull() ?: 1000
        val buildNumber = resolveConfigProperty("BUILD_NUMBER", "")
        val explicitVersionName = resolveConfigProperty("VERSION_NAME", "1.0.0")

        versionCode = explicitCode ?: if (buildNumber.isNotEmpty()) baseCode + (buildNumber.toIntOrNull() ?: 0) else baseCode
        versionName = if (buildNumber.isNotEmpty()) "$explicitVersionName-b$buildNumber" else explicitVersionName

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
    implementation(project(":core:base"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":core:firebase"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
