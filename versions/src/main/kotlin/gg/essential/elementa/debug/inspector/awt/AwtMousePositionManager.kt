package gg.essential.elementa.debug.inspector.awt

//#if MC<=11202
import gg.essential.elementa.manager.MousePositionManager
import gg.essential.elementa.manager.ResolutionManager
import org.jetbrains.annotations.ApiStatus

/**
 * A [MousePositionManager] that supplies its values from the supplied [frame]
 */
@ApiStatus.Internal
class AwtMousePositionManager(
    private val frame: AwtFrameBufferCanvas,
    private val resolutionManager: ResolutionManager,
) : MousePositionManager {

    override val rawX: Double
        get() = frame.mousePosition?.getX() ?: -1.0

    override val rawY: Double
        get() = frame.mousePosition?.getY() ?: -1.0

    override val scaledX: Double
        get() = rawX / resolutionManager.scaleFactor

    override val scaledY: Double
        get() = rawY / resolutionManager.scaleFactor
}
//#endif