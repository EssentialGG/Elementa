package gg.essential.elementa.manager

import gg.essential.universal.UResolution
import org.jetbrains.annotations.ApiStatus

/**
 * A resolution manager that provides its values from [UResolution].
 */
@ApiStatus.Internal
object DefaultResolutionManager : ResolutionManager {

    override val windowWidth: Int
        get() = UResolution.windowWidth

    override val windowHeight: Int
        get() = UResolution.windowHeight

    override val viewportWidth: Int
        get() = UResolution.viewportWidth

    override val viewportHeight: Int
        get() = UResolution.viewportHeight

    override val scaledWidth: Int
        get() = UResolution.scaledWidth

    override val scaledHeight: Int
        get() = UResolution.scaledHeight

    override val scaleFactor: Double
        get() = UResolution.scaleFactor
}