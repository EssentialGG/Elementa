package gg.essential.elementa.debug

import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.manager.ResolutionManager
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class ExternalResolutionManager(
    private val displayManager: ExternalInspectorDisplay,
) : ResolutionManager {

    override val windowWidth: Int
        get() = displayManager.getWidth()

    override val windowHeight: Int
        get() = displayManager.getHeight()

    override val viewportWidth: Int
        get() = windowWidth

    override val viewportHeight: Int
        get() = windowHeight

    override val scaledWidth: Int
        get() = windowWidth / scaleFactor.toInt()

    override val scaledHeight: Int
        get() = windowHeight / scaleFactor.toInt()

    override var scaleFactor: Double = 2.0
}