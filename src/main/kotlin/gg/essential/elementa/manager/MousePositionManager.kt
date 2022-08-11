package gg.essential.elementa.manager


/**
 * Provides a non-global way to access the cursor position.
 */
internal interface MousePositionManager {

    val rawX: Double

    val rawY: Double

    val scaledX: Double

    val scaledY: Double
}