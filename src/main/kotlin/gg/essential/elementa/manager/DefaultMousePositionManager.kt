package gg.essential.elementa.manager

import gg.essential.universal.UMouse
import org.jetbrains.annotations.ApiStatus

/**
 * A mouse position manager that provides its values from [UMouse]
 */
@ApiStatus.Internal
object DefaultMousePositionManager: MousePositionManager {

    override val rawX: Double
        get() = UMouse.Raw.x

    override val rawY: Double
        get() = UMouse.Raw.y

    override val scaledX: Double
        get() = UMouse.Scaled.x

    override val scaledY: Double
        get() = UMouse.Scaled.y
}