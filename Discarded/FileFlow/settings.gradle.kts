pluginManagement {
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
        // Primary Maven Central
        mavenCentral()
        // Direct fallback mirrors if primary CDN resolution fails
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

rootProject.name = "FileFlow"

include(":app")