package gg.essential.elementa.manager


/**
 * Provides a non-global way to access different aspects about the current resolution
 */
internal interface ResolutionManager {

    val windowWidth: Int

    val windowHeight: Int

    val viewportWidth: Int

    val viewportHeight: Int

    val scaledWidth: Int

    val scaledHeight: Int

    val scaleFactor: Double
}