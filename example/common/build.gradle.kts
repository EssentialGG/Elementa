import gg.essential.gradle.multiversion.StripReferencesTransform.Companion.registerStripReferencesAttribute

plugins {
    kotlin("jvm")
    id("gg.essential.defaults")
}
repositories.mavenLocal()

kotlin.jvmToolchain(8)

val common = registerStripReferencesAttribute("common") {
    excludes.add("net.minecraft")
}

dependencies {
    api(libs.kotlin.stdlib.jdk8)

    compileOnly(libs.versions.universalcraft.map { "gg.essential:universalcraft-1.8.9-forge:$it" }) {
        attributes { attribute(common, true) }
    }

    api(project(":"))
}
