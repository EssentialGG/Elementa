import gg.essential.gradle.multiversion.StripReferencesTransform.Companion.registerStripReferencesAttribute
import gg.essential.gradle.util.*

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    id("org.jetbrains.dokka") version "1.6.10" apply false
    id("gg.essential.defaults")
}

kotlin.jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
}
tasks.compileKotlin.setJvmDefault("all-compatibility")

val internal = makeConfigurationForInternalDependencies {
    relocate("org.dom4j", "gg.essential.elementa.impl.dom4j")
    relocate("org.commonmark", "gg.essential.elementa.impl.commonmark")
    remapStringsIn("org.dom4j.DocumentFactory")
}

val common = registerStripReferencesAttribute("common") {
    excludes.add("net.minecraft")
}

dependencies {
    val kotlin_version = "1.5.10"
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    compileOnly("org.jetbrains:annotations:23.0.0")

    internal("org.commonmark:commonmark:0.17.1")
    internal("org.commonmark:commonmark-ext-gfm-strikethrough:0.17.1")
    internal("org.commonmark:commonmark-ext-ins:0.17.1")
    internal("org.dom4j:dom4j:2.1.1")

    // Depending on LWJGL3 instead of 2 so we can choose opengl bindings only
    compileOnly("org.lwjgl:lwjgl-opengl:3.3.1")
    // Depending on 1.8.9 for all of these because that's the oldest version we support
    compileOnly("gg.essential:universalcraft-1.8.9-forge:202") {
        attributes { attribute(common, true) }
    }
    compileOnly("com.google.code.gson:gson:2.2.4")
}

apiValidation {
    ignoredProjects.add("platform")
    ignoredPackages.add("com.example")
    nonPublicMarkers.add("org.jetbrains.annotations.ApiStatus\$Internal")
}
