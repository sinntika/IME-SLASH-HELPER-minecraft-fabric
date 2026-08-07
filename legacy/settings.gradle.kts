pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases")
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "ime-slash-helper-legacy"

stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.20.4", "1.20.6", "1.21.1", "1.21.4", "1.21.8", "1.21.11")
        vcsVersion = "1.21.1"
    }
}
