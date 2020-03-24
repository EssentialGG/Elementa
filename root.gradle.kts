plugins {
    kotlin("jvm") version "1.3.61" apply false
//    id("fabric-loom") version "0.2.6-SNAPSHOT" apply false
    id("com.replaymod.preprocess") version "8385acb"
}


// Loom tries to find the active mixin version by recursing up to the root project and checking each project's
// compileClasspath and build script classpath (in that order). Since we've loom in our root project's classpath,
// loom will only find it after checking the root project's compileClasspath (which doesn't exist by default).
configurations.register("compileClasspath")

preprocess {
    //    "1.15.2"(11502, "srg") {
//        "1.12.2"(11202, "srg",file("versions/1.15.2-1.12.2.txt")) {
    "1.12.2"(11202, "srg") {
        "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
//    }

}