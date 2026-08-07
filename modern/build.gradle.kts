plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

val mcVersion = property("minecraft_version") as String
val modJava = (property("mod_java") as String).toInt()

version = "${property("mod_version")}+mc$mcVersion"
group = property("maven_group") as String

base {
    archivesName.set("${property("archives_base_name")}-$mcVersion")
}

// 共通ソースツリー（リポジトリ直下の src/）を参照する
val sharedSrc = rootDir.parentFile.resolve("src")
sourceSets.named("main") {
    java.setSrcDirs(listOf(sharedSrc.resolve("main/java")))
    resources.setSrcDirs(listOf(sharedSrc.resolve("main/resources")))
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // JNA は Minecraft 本体が同梱しているので compileOnly。
    // 環境によっては jar-in-jar で同梱する（下の include を有効化）。
    compileOnly("net.java.dev.jna:jna:${property("jna_version")}")
    compileOnly("net.java.dev.jna:jna-platform:${property("jna_version")}")
    // include(implementation("net.java.dev.jna:jna:${property("jna_version")}")!!)
}

val expandProps = mapOf(
    "version" to version.toString(),
    "minecraft_dependency" to (project.property("minecraft_dependency") as String),
    "mixin_compat" to (project.property("mixin_compat") as String),
    "java_version" to (project.property("mod_java") as String)
)

tasks.processResources {
    inputs.properties(expandProps)
    filesMatching(listOf("fabric.mod.json", "imeslash.client.mixins.json")) {
        expand(expandProps)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(modJava)
}

loom {
    runs {
        named("client") {
            vmArg("--enable-native-access=ALL-UNNAMED")
        }
    }
}
