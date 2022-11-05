package gg.essential.elementa.components.inspector.display.awt

import gg.essential.elementa.manager.MousePositionManager
import gg.essential.elementa.manager.ResolutionManager
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

/**
 * A [MousePositionManager] that supplies its values from the supplied [frame]
 */
internal class AwtMousePositionManager(
    private val frame: AwtFrameBufferCanvas,
    private val resolutionManager: ResolutionManager,
) : MousePositionManager, MouseMotionListener {

    init {
        frame.addMouseMotionListener(this)
    }

    override var rawX: Double = -1.0

    override var rawY: Double = -1.0

    override val scaledX: Double
        get() = rawX / resolutionManager.scaleFactor

    override val scaledY: Double
        get() = rawY / resolutionManager.scaleFactor

    override fun mouseDragged(e: MouseEvent) {
        rawX = e.x.toDouble()
        rawY = e.y.toDouble()
    }

    override fun mouseMoved(e: MouseEvent) {
        rawX = e.x.toDouble()
        rawY = e.y.toDouble()
    }
}