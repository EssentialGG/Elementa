plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("fabric-loom") version "0.8-SNAPSHOT" apply false
    id("com.replaymod.preprocess") version "ff216cd"
}

version = determineVersion()

// Loom tries to find the active mixin version by recursing up to the root project and checking each project's
// compileClasspath and build script classpath (in that order). Since we've loom in our root project's classpath,
// loom will only find it after checking the root project's compileClasspath (which doesn't exist by default).
configurations.register("compileClasspath")

preprocess {
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val fabric11602 = createNode("1.16.2-fabric", 11602, "yarn")
    val forge11602 = createNode("1.16.2", 11602, "srg")
    val forge11502 = createNode("1.15.2", 11502, "srg")
    val forge11202 = createNode("1.12.2", 11202, "srg")
    val forge10809 = createNode("1.8.9", 10809, "srg")

    fabric11701.link(fabric11602)
    fabric11602.link(forge11602)
    forge11602.link(forge11502)
    forge11502.link(forge11202, file("versions/1.15.2-1.12.2.txt"))
    forge11202.link(forge10809, file("versions/1.12.2-1.8.9.txt"))
}

fun determineVersion(): String {
    val branch = branch()
    var version = buildId() ?: return "$branch-SNAPSHOT"
    if (branch != "master") {
        version += "+$branch"
    }
    return version
}
fun buildId(): String? = project.properties["BUILD_ID"]?.toString()
fun branch(): String = project.properties["branch"]?.toString() ?: try {
    val stdout = java.io.ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
} catch (e: Throwable) {
    "unknown"
}
