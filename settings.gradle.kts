pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-bedrock"

// Core modules
include(":core:base")
include(":core:navigation")
include(":core:network")
include(":core:ui")
include(":core:datastore")

// Application entry
include(":app")
