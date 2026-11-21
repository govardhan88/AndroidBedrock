plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.sonar)
    id("bedrock.scaffolding")
}

sonar {
    properties {
        property("sonar.projectKey", "govardhan88_AndroidBedrock")
        property("sonar.organization", "govardhan88")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/coverage/test/debug/report.xml")
        property(
            "sonar.coverage.exclusions",
            "**/di/**,**/*Activity*.*,**/*Screen*.*,**/*Preview*.*,**/*Theme*.*,**/*Dto*.*,**/*Route*.*,**/BuildConfig.*",
        )

        val localProps = file("local.properties")
        val localToken =
            if (localProps.exists()) {
                val props = java.util.Properties()
                localProps.inputStream().use { props.load(it) }
                props.getProperty("SONAR_TOKEN") ?: props.getProperty("sonar.token")
            } else {
                null
            }

        val token =
            providers.environmentVariable("SONAR_TOKEN").orNull?.takeIf { it.isNotBlank() }
                ?: localToken?.takeIf { it.isNotBlank() }
                ?: providers.gradleProperty("sonar.token").orNull

        if (!token.isNullOrBlank()) {
            property("sonar.token", token)
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint("1.3.1").setEditorConfigPath("$rootDir/.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint("1.3.1").setEditorConfigPath("$rootDir/.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register<Exec>("installGitHooks") {
    description = "Installs Git pre-commit hooks for auto-formatting and zero-warning policy"
    group = "verification"
    workingDir = rootDir
    commandLine("git", "config", "core.hooksPath", ".githooks")
    doLast {
        val hookFile = file(".githooks/pre-commit")
        if (hookFile.exists()) {
            hookFile.setExecutable(true, false)
        }
        println("✓ Git pre-commit hooks successfully configured at .githooks/")
    }
}
