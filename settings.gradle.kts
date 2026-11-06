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

rootProject.name = "govi-clean-architecture-template"

// Core modules
include(":core:navigation")
include(":core:network")

// Feature Users
include(":feature:users:domain")
include(":feature:users:data")
include(":feature:users:presentation")

// Application entry
include(":app")

// Feature: products
include(":feature:products:domain")
include(":feature:products:data")
include(":feature:products:presentation")
