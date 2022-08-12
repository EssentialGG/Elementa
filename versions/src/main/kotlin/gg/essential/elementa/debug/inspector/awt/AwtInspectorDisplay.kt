package gg.essential.elementa.debug.inspector.awt

//#if MC<=11202
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.debug.ExternalResolutionManager
import gg.essential.elementa.debug.FrameBufferedWindow
import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.manager.ResolutionManager
import org.jetbrains.annotations.ApiStatus
import java.awt.Dimension
import java.util.concurrent.Executors
import javax.swing.JFrame
import javax.swing.WindowConstants


/**
 * Implementation for [ExternalInspectorDisplay] using Java AWT for LWJGL2
 */
@ApiStatus.Internal
class AwtInspectorDisplay : ExternalInspectorDisplay {

    private val window = Window(ElementaVersion.V2)
    private val buffer = FrameBufferedWindow(window, this)
    private val frame = JFrame("Inspector")
    private val canvas = AwtFrameBufferCanvas(buffer)
    private val resolutionManager = ExternalResolutionManager(this)
    private val threadPool = Executors.newFixedThreadPool(1)

    private val listenerAdaptor = AwtEventListenerAdaptor(resolutionManager, window)

    init {
        window.resolutionManager = resolutionManager
        window.keyboardManager = listenerAdaptor
        frame.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        frame.add(canvas)
        val height = 480
        canvas.preferredSize = Dimension(height * 16 / 9, height)
        frame.pack()
        window.mousePositionManager = AwtMousePositionManager(canvas, resolutionManager)
        canvas.addMouseListener(listenerAdaptor)
        canvas.addMouseWheelListener(listenerAdaptor)
        canvas.addKeyListener(listenerAdaptor)
    }

    override val visible: Boolean
        get() = frame.isVisible

    override fun updateVisiblity(visible: Boolean) = threadPool.execute {
        frame.isVisible = visible
    }

    override fun addComponent(component: UIComponent) {
        if (!visible) {
            updateVisiblity(true)
        }
        window.addChild(component)
    }

    override fun removeComponent(component: UIComponent) {
        window.removeChild(component)
        if (window.children.isEmpty()) {
            updateVisiblity(false)
        }
    }

    override fun getWidth(): Int {
        return canvas.width
    }

    override fun getHeight(): Int {
        return canvas.height
    }

    override fun updateFrameBuffer(resolutionManager: ResolutionManager) {
        buffer.updateFrameBuffer(resolutionManager)
    }

    override fun cleanup() {
        frame.dispose()
        threadPool.shutdownNow()
        buffer.deleteFrameBuffer()
    }

}
//#endif