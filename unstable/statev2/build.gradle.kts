import gg.essential.gradle.util.setJvmDefault
import gg.essential.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm")
    id("gg.essential.defaults")
    id("maven-publish")
}

version = versionFromBuildIdAndBranch()
group = "gg.essential"

dependencies {
    compileOnly(project(":"))
}
tasks.compileKotlin.setJvmDefault("all-compatibility")

kotlin.jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])

            artifactId = "elementa-unstable-${project.name}"
        }
    }
}