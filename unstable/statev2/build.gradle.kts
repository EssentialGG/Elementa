import gg.essential.gradle.multiversion.StripReferencesTransform.Companion.registerStripReferencesAttribute
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

    val common = registerStripReferencesAttribute("common") {
        excludes.add("net.minecraft")
    }
    compileOnly(libs.versions.universalcraft.map { "gg.essential:universalcraft-1.8.9-forge:$it" }) {
        attributes { attribute(common, true) }
    }
}
tasks.compileKotlin.setJvmDefault("all")

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