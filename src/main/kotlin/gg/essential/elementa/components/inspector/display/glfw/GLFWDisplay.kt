package gg.essential.elementa.components.inspector.display.glfw

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.debug.ExternalResolutionManager
import gg.essential.elementa.debug.FrameBufferedWindow
import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.manager.ResolutionManager
import org.lwjgl.glfw.GLFW.*

internal class GLFWDisplay : ExternalInspectorDisplay {

    private val window = Window(ElementaVersion.V2)
    private val buffer = FrameBufferedWindow(window, this)
    private val windowPointer = try {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwCreateWindow(852, 480, "Inspector", 0, glfwGetCurrentContext())
    } finally {
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE)
    }
    private val renderer = GLFWRenderer(buffer, windowPointer, this)
    private val resolutionManager = ExternalResolutionManager(this)
    private val inputReceiver = GLFWInputReceiver(windowPointer, resolutionManager, window)

    init {
        window.mousePositionManager = inputReceiver
        window.keyboardManager = inputReceiver
        window.resolutionManager = resolutionManager
    }

    override var visible: Boolean = false

    override fun updateVisiblity(visible: Boolean) {
        this.visible = visible
        if (visible) {
            glfwShowWindow(windowPointer)
        } else {
            glfwHideWindow(windowPointer)
        }
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
        return getSize().first
    }

    private fun getSize(): Pair<Int, Int> {
        val widthArray = IntArray(1)
        val heightArray = IntArray(1)
        glfwGetWindowSize(windowPointer, widthArray, heightArray)
        return widthArray[0] to heightArray[0]
    }

    override fun getHeight(): Int {
        return getSize().second
    }

    override fun updateFrameBuffer(resolutionManager: ResolutionManager) {
        buffer.updateFrameBuffer(resolutionManager)
    }

    override fun cleanup() {
        glfwDestroyWindow(windowPointer)
    }

}