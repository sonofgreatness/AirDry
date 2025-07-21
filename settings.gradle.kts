pluginManagement {
    repositories {
        maven{url = uri("https://jitpack.io")}
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven{url = uri("https://jitpack.io")}
        mavenCentral()
        google()

    }
}

rootProject.name = "AirDry"
include(":app")
