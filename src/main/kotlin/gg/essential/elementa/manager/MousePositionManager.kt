package gg.essential.elementa.manager

import org.jetbrains.annotations.ApiStatus


/**
 * Provides a non-global way to access the cursor position.
 */
@ApiStatus.Internal
interface MousePositionManager {

    val rawX: Double

    val rawY: Double

    val scaledX: Double

    val scaledY: Double
}