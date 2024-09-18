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
        val egtVersion = "0.5.0"
        id("gg.essential.defaults") version egtVersion
        id("gg.essential.defaults.maven-publish") version egtVersion
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
    }
}

rootProject.name = "Elementa"


include(":mc-stubs")

include(":unstable:statev2")
include(":unstable:layoutdsl")


include(":example")
project(":example").apply {
    buildFileName = "root.gradle.kts"
}
include(":example:common")
listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.16.2-forge",
    "1.16.2-fabric",
    "1.17.1-fabric",
    "1.17.1-forge",
    "1.18.1-fabric",
    "1.18.1-forge",
).forEach { version ->
    include(":example:$version")
    project(":example:$version").apply {
        buildFileName = "../build.gradle.kts"
    }
}

