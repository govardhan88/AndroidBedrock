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
include(":core:base")
include(":core:navigation")
include(":core:network")
include(":core:ui")
include(":core:datastore")

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

// Feature: fixture
include(":feature:fixture:domain")
include(":feature:fixture:data")
include(":feature:fixture:presentation")

// Feature: settings (compact)
include(":feature:settings")
