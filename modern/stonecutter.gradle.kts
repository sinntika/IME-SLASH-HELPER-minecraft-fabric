plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.1.2" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

// 全バージョンをビルドして、リポジトリ直下の dist/ に jar を集める
tasks.register<Copy>("collectJars") {
    group = "project"
    dependsOn("chiseledBuild")
    from(fileTree(rootDir.resolve("versions")) {
        include("*/build/libs/*.jar")
        exclude("**/*-sources.jar", "**/*-dev.jar", "**/*-dev-shadow.jar")
    })
    into(rootDir.parentFile.resolve("dist"))
    eachFile { path = name }
    includeEmptyDirs = false
}
