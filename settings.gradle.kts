pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
    plugins {
        val egtVersion = "0.3.0"
        id("gg.essential.defaults") version egtVersion
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
    }
}

rootProject.name = "Elementa"

include(":platform")
project(":platform").apply {
    projectDir = file("versions/")
    buildFileName = "root.gradle.kts"
}
include(":unstable:statev2")
include(":unstable:layoutdsl")

listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.15.2-forge",
    "1.16.2-forge",
    "1.16.2-fabric",
    "1.17.1-fabric",
    "1.17.1-forge",
    "1.18.1-fabric",
    "1.18.1-forge",
).forEach { version ->
    include(":platform:$version")
    project(":platform:$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../build.gradle.kts"
    }
}

