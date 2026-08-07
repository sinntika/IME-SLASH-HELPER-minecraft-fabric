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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "ime-slash-helper-modern"

stonecutter {
    create(rootProject) {
        versions("26.1.2")
        vcsVersion = "26.1.2"
    }
}
