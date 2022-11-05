package gg.essential.elementa.manager

import org.jetbrains.annotations.ApiStatus


/**
 * Provides a non-global way to access different aspects about the current resolution
 */
@ApiStatus.Internal
interface ResolutionManager {

    val windowWidth: Int

    val windowHeight: Int

    val viewportWidth: Int

    val viewportHeight: Int

    val scaledWidth: Int

    val scaledHeight: Int

    val scaleFactor: Double
}